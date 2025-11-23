package apinexo.common.configuration;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingResponseWrapper;

import apinexo.common.utils.ApinexoUtils;
import apinexo.common.utils.RateLimitEnum;
import apinexo.core.modules.logs.entity.LogEntity;
import apinexo.core.modules.logs.service.LogService;
import apinexo.core.modules.subscription.entity.SubscriptionEntity;
import apinexo.core.modules.subscription.service.SubscriptionService;
import apinexo.core.modules.user.entity.UserEntity;
import apinexo.core.modules.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private static final String API_KEY_HEADER = "x-api-key";

    @Value("${fe.server}")
    private String feServer;

    @Autowired
    private UserService userService;

    @Autowired
    private LogService logService;

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private ApinexoUtils utils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        String apiKey = request.getHeader(API_KEY_HEADER);
        if (StringUtils.isBlank(apiKey)) {
            unauthorized(response);
            return false;
        }

        // check api key exist
        Optional<UserEntity> optionalUser = userService.findByApiKey(apiKey);
        if (optionalUser.isEmpty()) {
            authenticationError(response);
            return false;
        }

        // check user subscribe
        UserEntity userEntity = optionalUser.get();
        String apiId = resolveApiName(request.getRequestURI());
        Optional<SubscriptionEntity> optionalSubscription = subscriptionService.findByUserIdAndApiId(userEntity.getId(),
                apiId);
        if (optionalSubscription.isEmpty()) {
            authenticationError(response);
            return false;
        }

        // reset quota
        SubscriptionEntity subscriptionEntity = optionalSubscription.get();
        subscriptionService.updateBillingPeriod(subscriptionEntity);

        // check quota
        long quota = subscriptionEntity.getQuota();
        long quotaUsed = subscriptionEntity.getQuotaUsed();
        if (quotaUsed >= quota) {
            exceededQuotaError(response, subscriptionEntity.getCurrentPlan(), subscriptionEntity.getApi().getId());
            return false;
        }

        // check rate limit
        boolean isRateLimit = subscriptionEntity.getIsRateLimit();
        if (isRateLimit) {
            Long rateLimit = subscriptionEntity.getRateLimit();
            String rateLimitPeriod = subscriptionEntity.getRateLimitPeriod();
            long now = utils.milliseconds();
            RateLimitEnum period = RateLimitEnum.valueOf(rateLimitPeriod.toUpperCase());
            long startTime = now - period.toMillis();
            long count = logService.countRequests(subscriptionEntity.getId(), startTime);
            if (count >= rateLimit) {
                rateLimitError(response, rateLimit, rateLimitPeriod);
                return false;
            }
        }

        // subscriptionId
        request.setAttribute("subscription_id", optionalSubscription.get().getId());

        // email
        request.setAttribute("email", userEntity.getEmail());

        // user_name
        request.setAttribute("user_name", "");

        // first_name
        request.setAttribute("first_name", userEntity.getFirstName());

        // last_name
        request.setAttribute("last_name", userEntity.getLastName());

        // location
        request.setAttribute("location", "");

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        // id
        String id = utils.uuidRandom();

        // subscription_id
        String subscriptionId = (String) request.getAttribute("subscription_id");

        // time
        Long time = utils.milliseconds();

        // user_name
        String username = (String) request.getAttribute("user_name");

        // email
        String email = (String) request.getAttribute("email");

        // first_name
        String firstName = (String) request.getAttribute("first_name");

        // last_name
        String lastName = (String) request.getAttribute("last_name");

        // endpoint
        String endpoint = request.getRequestURI();

        // method
        String method = request.getMethod();

        // location
        String location = (String) request.getAttribute("location");

        // response_status
        Integer responseStatus = response.getStatus();

        // latency
        long start = (long) request.getAttribute("startTime");
        String latency = String.format("%,dms", (System.currentTimeMillis() - start));

        // request_headers
        Map<String, String> headers = Collections.list(request.getHeaderNames()).stream()
                .collect(Collectors.toMap(h -> h, request::getHeader));
        String requestHeaders = utils.convertDtoToJson(headers).toString();

        // request_query_parameters
        String requestQueryParameters = request.getQueryString() != null ? request.getQueryString() : "";

        // request_body
        String requestBody = "";
        if (request instanceof CachedBodyHttpServletRequest w) {
            requestBody = new String(w.getCachedBody(), request.getCharacterEncoding());
        }

        // response_headers
        String responseHeaders = "";

        // response_body
        String responseBody = "";
        if (response instanceof ContentCachingResponseWrapper w) {
            byte[] arr = w.getContentAsByteArray();
            responseBody = new String(arr, response.getCharacterEncoding());
            if (StringUtils.isNotBlank(responseBody) && responseBody.length() > 1000) {
                responseBody = responseBody.substring(0, 1000);
            }
        }

        LogEntity entity = LogEntity.builder().id(id).subscriptionId(subscriptionId).time(time).username(username)
                .email(email).firstName(firstName).lastName(lastName).endpoint(endpoint).method(method)
                .location(location).responseStatus(responseStatus).latency(latency).requestHeaders(requestHeaders)
                .requestQueryParameters(requestQueryParameters).requestBody(requestBody)
                .responseHeaders(responseHeaders).responseBody(responseBody).build();
        this.saveDb(entity, subscriptionId);
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }

    @Transactional
    private void saveDb(LogEntity entity, String subscriptionId) {
        logService.save(entity);
        subscriptionService.increaseQuotaUsed(subscriptionId);
    }

    private void unauthorized(HttpServletResponse response) throws IOException {
        response.setStatus(401);
        response.setContentType("application/json");
        response.getWriter().write("{\"message\": \"Invalid API key.\"}");
        response.getWriter().flush();
    }

    private void authenticationError(HttpServletResponse response) throws IOException {
        response.setStatus(403);
        response.setContentType("application/json");
        response.getWriter().write("{\"message\": \"You are not subscribed to this API.\"}");
        response.getWriter().flush();
    }

    private void exceededQuotaError(HttpServletResponse response, String currentPlan, String apiId) throws IOException {
        response.setStatus(429);
        response.setContentType("application/json");
        response.getWriter()
                .write("{\"message\": \"You have exceeded the MONTHLY quota for Requests on your current plan, "
                        + currentPlan.toUpperCase() + ". Upgrade your plan at " + feServer + "/api/" + apiId
                        + "/pricing\"}");
        response.getWriter().flush();
    }

    private void rateLimitError(HttpServletResponse response, Long rateLimit, String rateLimitPeriod)
            throws IOException {
        response.setStatus(429);
        response.setContentType("application/json");
        response.getWriter().write(
                "{\"message\": \"Rate limit exceeded (" + rateLimit + " requests per " + rateLimitPeriod + ").\"}");
        response.getWriter().flush();
    }

    private String resolveApiName(String url) {
        try {
            URI uri = new URI(url);
            String path = uri.getPath();
            String[] parts = path.split("/");
            if (parts.length > 1) {
                return parts[1];
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
