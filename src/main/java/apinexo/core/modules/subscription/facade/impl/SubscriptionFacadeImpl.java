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
//            String planKey = (String) body.get("planKey");
//            String userEmail = (String) body.get("email");
//            String userId = (String) body.get("userId");
            String planKey = "pro";
            String userEmail = "marketing202001@gmail.com";
            String userId = utils.uuidRandom();

            if (planKey == null || userEmail == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Missing planKey or email"));
            }

            Stripe.apiKey = stripeSecretKey;

            String priceId = switch (planKey) {
            case "basic" -> "price_basic_id";
            case "pro" -> "price_1SLgjuR5bl4Qw3RDJTJDzfDB";
            case "ultra" -> "price_1S3YCmRoRAtc95u19GnkLda0";
            case "mega" -> "price_1S3YCnRoRAtc95u1LW4pDadp";
            default -> throw new IllegalArgumentException("Invalid plan key");
            };

            SessionCreateParams params = SessionCreateParams.builder().setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                    .setCustomerEmail(userEmail)
                    .setSuccessUrl("https://your-frontend.com/success?session_id={CHECKOUT_SESSION_ID}")
                    .setCancelUrl("https://your-frontend.com/cancel")
                    .addLineItem(SessionCreateParams.LineItem.builder().setPrice(priceId).setQuantity(1L).build())
                    .putMetadata("userId", userId).putMetadata("planKey", planKey).build();

            Session session = Session.create(params);

            return ResponseEntity.ok(Map.of("checkout_url", session.getUrl(), "session_id", session.getId()));
//            JsonNode session = openmeterService.stripeCheckoutSessions();
//            return ResponseEntity.ok(session);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}