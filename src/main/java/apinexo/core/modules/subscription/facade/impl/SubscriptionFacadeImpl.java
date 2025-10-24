package apinexo.core.modules.subscription.facade.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;

import apinexo.core.modules.openmeter.service.OpenmeterService;
import apinexo.core.modules.subscription.facade.SubscriptionFacade;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SubscriptionFacadeImpl implements SubscriptionFacade {

    private final OpenmeterService openmeterService;

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @Override
    public ResponseEntity<Object> changeSubscription(Jwt jwt, Map<String, Object> body) {
        try {
            JsonNode session = openmeterService.stripeCheckoutSessions();
            return ResponseEntity.ok(session);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

}
