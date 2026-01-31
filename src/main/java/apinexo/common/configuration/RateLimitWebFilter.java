package apinexo.common.configuration;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.fasterxml.jackson.databind.ObjectMapper;

import apinexo.common.redis.RateLimitService;
import apinexo.common.service.LogSyncService;
import apinexo.common.service.QuotaSyncService;
import apinexo.common.utils.ApinexoUtils;
import apinexo.common.utils.RateLimitEnum;
import apinexo.core.modules.logs.entity.LogEntity;
import apinexo.core.modules.stripe.service.StripeService;
import apinexo.core.modules.subscription.dto.SubscriptionCached;
import apinexo.core.modules.subscription.service.SubscriptionService;
import apinexo.core.modules.user.entity.UserEntity;
import apinexo.core.modules.user.service.UserService;
import reactor.core.publisher.Mono;

@Component
public class RateLimitWebFilter implements WebFilter {

    private static final String API_KEY_HEADER = "x-api-key";

    @Value("${fe.server}")
    private String feServer;

    @Autowired
    private UserService userService;

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private StripeService stripeService;

    @Autowired
    private ApinexoUtils utils;

    @Autowired
    private RateLimitService rateLimitService;

    @Autowired
    private QuotaSyncService quotaSyncService;

    @Autowired
    private LogSyncService logSyncService;

    @Autowired
    private StringRedisTemplate redis;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        String path = request.getPath().value();
        if (path.startsWith("/dev/") || path.startsWith("/portal/") || path.equals("/error")) {
            return chain.filter(exchange);
        }

        String apiKey = request.getHeaders().getFirst(API_KEY_HEADER);
        if (StringUtils.isBlank(apiKey)) {
            return writeError(response, HttpStatus.UNAUTHORIZED, "Invalid API key.");
        }

        return Mono.fromCallable(() -> {
            String key = "user:apikey:" + apiKey;
            String cached = redis.opsForValue().get(key);
            if (cached != null) {
                return Optional.of(objectMapper.readValue(cached, UserEntity.class));
            }
            Optional<UserEntity> userOpt = userService.findByApiKey(apiKey);
            userOpt.ifPresent(user -> {
                try {
                    redis.opsForValue().set(key, objectMapper.writeValueAsString(user), Duration.ofMinutes(10));
                } catch (Exception e) {
                }
            });
            return userOpt;
        }).flatMap(optionalUser -> {

            if (optionalUser.isEmpty()) {
                return writeError(response, HttpStatus.FORBIDDEN, "You are not subscribed to this API.");
            }

            UserEntity user = optionalUser.get();
            String apiId = resolveApiName(path);

            Optional<SubscriptionCached> optSub = subscriptionService.getSubscriptionCached(user.getId(),
                    apiId);

            if (optSub.isEmpty()) {
                return writeError(response, HttpStatus.FORBIDDEN, "You are not subscribed to this API.");
            }

            SubscriptionCached sub = optSub.get();

            // reset quota
            subscriptionService.updateBillingPeriodFree(sub);

            // quota check
            if (!sub.getIsSoftLimit() && sub.getQuotaUsed() >= sub.getQuota()) {
                return writeError(response, HttpStatus.TOO_MANY_REQUESTS,
                        "You have exceeded the MONTHLY quota for Requests on your current plan, "
                                + sub.getCurrentPlan().toUpperCase() + ". Upgrade at " + feServer + "/api/"
                                + sub.getApiId());
            }

            // soft limit
            if (sub.getIsSoftLimit() && sub.getQuotaUsed() > sub.getQuota()) {
                long overage = sub.getQuotaUsed() - sub.getQuota();
                if (overage % 50 == 0) {
                    stripeService.reportUsage(sub.getSubscriptionItemId(), 50);
                }
            }

            // rate limit
            if (sub.getIsRateLimit()) {
                RateLimitEnum period = RateLimitEnum.valueOf(sub.getRateLimitPeriod().toUpperCase());
                long count = rateLimitService.incrementAndGet(sub.getId(), period.toSeconds());
                if (count > sub.getRateLimit()) {
                    return writeError(response, HttpStatus.TOO_MANY_REQUESTS, "Rate limit exceeded ("
                            + sub.getRateLimit() + " requests per " + sub.getRateLimitPeriod() + ").");
                }
            }

            // store attributes (WebFlux style)
            exchange.getAttributes().put("subscription_id", sub.getId());
            exchange.getAttributes().put("email", user.getEmail());
            exchange.getAttributes().put("user_name", user.getUserName());
            exchange.getAttributes().put("first_name", user.getFirstName());
            exchange.getAttributes().put("last_name", user.getLastName());
            exchange.getAttributes().put("location", "");
            exchange.getAttributes().put("startTime", System.currentTimeMillis());
            response.beforeCommit(() -> {
                HttpHeaders headers = response.getHeaders();
                headers.add("Access-Control-Expose-Headers", "X-Quota, X-Quota-Used, X-Quota-Remaining, X-Rate-Limit");
                Long rateLimit = sub.getRateLimit();
                String rateLimitPeriod = sub.getRateLimitPeriod();
                if (rateLimit != null && StringUtils.isNotBlank(rateLimitPeriod)) {
                    headers.set("X-Rate-Limit", rateLimit + " requests per " + rateLimitPeriod);
                }
                long quotaUsed = sub.getQuotaUsed() + quotaSyncService.getQuotaOrZero(sub.getId()) + 1;
                headers.set("X-Quota", String.valueOf(sub.getQuota()));
                headers.set("X-Quota-Used", String.valueOf(quotaUsed));
                headers.set("X-Quota-Remaining", String.valueOf(sub.getQuota() - quotaUsed));
                return Mono.empty();
            });
            return chain.filter(exchange).doFinally(signal -> {
                quotaSyncService.increaseQuota(sub.getId());
                saveLog(exchange);
            });
        });
    }

    private Mono<Void> writeError(ServerHttpResponse response, HttpStatus status, String message) {

        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        byte[] bytes = ("{\"message\":\"" + message + "\"}").getBytes(StandardCharsets.UTF_8);

        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(buffer));
    }

    private void saveLog(ServerWebExchange exchange) {

        // subscription_id
        String subscriptionId = (String) exchange.getAttribute("subscription_id");
        if (subscriptionId == null)
            return;

        // user_name
        String username = (String) exchange.getAttribute("user_name");

        // email
        String email = (String) exchange.getAttribute("email");

        // first_name
        String firstName = (String) exchange.getAttribute("first_name");

        // last_name
        String lastName = (String) exchange.getAttribute("last_name");

        // end point
        String endpoint = exchange.getRequest().getPath().value();

        // method
        String method = exchange.getRequest().getMethod().name();

        // location
        String location = (String) exchange.getAttribute("location");

        // response_status
        Integer responseStatus = exchange.getResponse().getStatusCode() != null
                ? exchange.getResponse().getStatusCode().value()
                : 200;

        // latency
        long start = (long) exchange.getAttribute("startTime");
        String latency = String.format("%,dms", (System.currentTimeMillis() - start));

        // request_headers
        String requestHeaders = exchange.getRequest().getHeaders().toSingleValueMap().toString();

        // request_query_parameters
        String requestQueryParameters = exchange.getRequest().getQueryParams().toSingleValueMap().toString();

        // request_body
        String requestBody = (String) exchange.getAttribute("request_body");
        if (StringUtils.isNotBlank(requestBody) && requestBody.length() > 1000) {
            requestBody = requestBody.substring(0, 1000);
        }

        // response_headers
        String responseHeaders = exchange.getResponse().getHeaders().toSingleValueMap().toString();

        // response_body
        String responseBody = (String) exchange.getAttribute("response_body");
        if (StringUtils.isNotBlank(responseBody) && responseBody.length() > 1000) {
            responseBody = responseBody.substring(0, 1000);
        }

        LogEntity entity = LogEntity.builder().id(utils.uuidRandom()).subscriptionId(subscriptionId)
                .time(utils.milliseconds()).username(username).email(email).firstName(firstName).lastName(lastName)
                .endpoint(endpoint).method(method).location(location).responseStatus(responseStatus).latency(latency)
                .requestHeaders(requestHeaders).requestQueryParameters(requestQueryParameters).requestBody(requestBody)
                .requestBody(responseHeaders).responseBody(responseBody).build();
        logSyncService.push(entity);
    }

    private String resolveApiName(String path) {
        String[] parts = path.split("/");
        return parts.length > 1 ? parts[1] : null;
    }
}
