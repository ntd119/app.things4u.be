package apinexo.core.modules.subscription.facade.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import apinexo.common.utils.ApinexoUtils;
import apinexo.core.modules.subscription.facade.SubscriptionFacade;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SubscriptionFacadeImpl implements SubscriptionFacade {

    // private final OpenmeterService openmeterService;

    private final ApinexoUtils utils;

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @Override
    public ResponseEntity<Object> changeSubscription(Jwt jwt, Map<String, Object> body) {
        try {
            String userEmail = "marketing202001@gmail.com";
            String userId = utils.uuidRandom();
            Stripe.apiKey = stripeSecretKey;
            String priceId = "price_1SLgjuR5bl4Qw3RDJTJDzfDB";
            SessionCreateParams params = SessionCreateParams.builder().setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                    .setCustomerEmail(userEmail)
                    .setSuccessUrl("https://your-frontend.com/success?session_id={CHECKOUT_SESSION_ID}")
                    .setCancelUrl("https://your-frontend.com/cancel")
                    .addLineItem(SessionCreateParams.LineItem.builder().setPrice(priceId).setQuantity(1L).build())
                    .putMetadata("userId", userId).putMetadata("planKey", "Pro").build();

            Session session = Session.create(params);

            return ResponseEntity.ok(Map.of("checkout_url", session.getUrl(), "session_id", session.getId()));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}