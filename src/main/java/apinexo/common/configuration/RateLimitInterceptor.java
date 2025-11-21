package apinexo.common.configuration;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import apinexo.core.modules.openmeter.service.OpenmeterService;
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
    private SubscriptionService subscriptionService;

    @Autowired
    private OpenmeterService openmeterService;

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

        openmeterService.events(apiId, userEntity.getId());
        return true;
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
