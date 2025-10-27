package apinexo.core.modules.subscription.facade;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;

import apinexo.core.modules.subscription.dto.SubscriptionChangeSubscriptionRequest;

public interface SubscriptionFacade {

    public ResponseEntity<Object> changeSubscription(Jwt jwt, SubscriptionChangeSubscriptionRequest body);

    public ResponseEntity<Object> getSubscriptions(Jwt jwt);
}
