package apinexo.common.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import apinexo.core.modules.subscription.entity.SubscriptionEntity;
import apinexo.core.modules.subscription.service.SubscriptionService;
import apinexo.core.modules.user.entity.UserEntity;
import apinexo.core.modules.user.service.UserService;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;
import java.util.UUID;

@Component
public class ApiKeyFilter extends OncePerRequestFilter {

    private static final String API_KEY_HEADER = "x-api-key";

    @Autowired
    private UserService userService;

    @Autowired
    private SubscriptionService subscriptionService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        if (path.startsWith("/dev/") || path.startsWith("/portal/")) {
            filterChain.doFilter(request, response);
            return;
        }

        String apiKey = request.getHeader(API_KEY_HEADER);

        if (StringUtils.isBlank(apiKey)) {
            unauthorized(response);
            return;
        }

        Optional<UserEntity> optionalUser = userService.findByApiKey(apiKey);
        if (optionalUser.isEmpty()) {
            authenticationError(response);
            return;
        }

        UserEntity userEntity = optionalUser.get();
        String apiId = resolveApiName(request.getRequestURI());
        Optional<SubscriptionEntity> optionalSubscription = subscriptionService.findByUserIdAndApiId(userEntity.getId(),
                apiId);
        if (optionalSubscription.isEmpty()) {
            authenticationError(response);
            return;
        }

        filterChain.doFilter(request, response);
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

    private void unauthorized(HttpServletResponse response) throws IOException {
        response.setStatus(401);
        response.setContentType("application/json");
        response.getWriter().write("{\"message\": \"Unauthorized\"}");
    }

    private void authenticationError(HttpServletResponse response) throws IOException {
        response.setStatus(401);
        response.setContentType("application/json");
        String json = String.format("""
                {
                  "status": "ERROR",
                  "request_id": "%s",
                  "error": {
                    "message": "Authentication error",
                    "code": 401
                  }
                }
                """, UUID.randomUUID().toString());
        response.getWriter().write(json);
    }
}
