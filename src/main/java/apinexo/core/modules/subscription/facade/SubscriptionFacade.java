package apinexo.core.modules.subscription.facade;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;

public interface SubscriptionFacade {

    public ResponseEntity<Object> changeSubscription(Jwt jwt, Map<String, Object> body);
}
