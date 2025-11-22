package apinexo.common.configuration;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import apinexo.common.utils.ApinexoUtils;
import apinexo.core.modules.logs.entity.LogEntity;
import apinexo.core.modules.logs.service.LogService;
import apinexo.core.modules.subscription.entity.SubscriptionEntity;
import apinexo.core.modules.subscription.service.SubscriptionService;
import apinexo.core.modules.user.entity.UserEntity;
import apinexo.core.modules.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private static final String API_KEY_HEADER = "x-api-key";

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

        Optional<UserEntity> optionalUser = userService.findByApiKey(apiKey);
        if (optionalUser.isEmpty()) {
            authenticationError(response);
            return false;
        }

        UserEntity userEntity = optionalUser.get();
        String apiId = resolveApiName(request.getRequestURI());
        Optional<SubscriptionEntity> optionalSubscription = subscriptionService.findByUserIdAndApiId(userEntity.getId(),
                apiId);
        if (optionalSubscription.isEmpty()) {
            authenticationError(response);
            return false;
        }

        // subscriptionId
        request.setAttribute("subscriptionId", optionalSubscription.get().getId());

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        // id
        String id = utils.uuidRandom();

        // subscription_id
        String subscriptionId = (String) request.getAttribute("subscriptionId");

        // time
        Long time = utils.milliseconds();

        // user_name
        String username = "";

        // email
        String email = "";

        // first_name
        String firstName = "";

        // last_name
        String lastName = "";

        // endpoint
        String endpoint = request.getRequestURI();

        // method
        String method = request.getMethod();

        // location
        String location = "";

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
        if (response instanceof CachedBodyHttpServletResponse w) {
            responseBody = new String(w.getCachedBody(), response.getCharacterEncoding());
        }

        LogEntity entity = LogEntity.builder().id(id).subscriptionId(subscriptionId).time(time).username(username)
                .email(email).firstName(firstName).lastName(lastName).endpoint(endpoint).method(method)
                .location(location).responseStatus(responseStatus).latency(latency).requestHeaders(requestHeaders)
                .requestQueryParameters(requestQueryParameters).requestBody(requestBody)
                .responseHeaders(responseHeaders).responseBody(responseBody).build();
        logService.save(entity);
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
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
