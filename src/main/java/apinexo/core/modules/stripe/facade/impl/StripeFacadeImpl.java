package apinexo.core.modules.stripe.facade.impl;

import java.util.Scanner;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;

import apinexo.common.dtos.AbstractService;
import apinexo.common.utils.ApinexoUtils;
import apinexo.core.modules.stripe.facade.StripeFacade;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StripeFacadeImpl extends AbstractService implements StripeFacade {

    private final ApinexoUtils utils;

    @Value("${stripe.secret.endpoint}")
    private String stripeSecretEndpoint;

    @Override
    public ResponseEntity<Object> webhook(HttpServletRequest request) {
        try {
            String payload = new Scanner(request.getInputStream(), "UTF-8").useDelimiter("\\A").next();
            String sigHeader = request.getHeader("Stripe-Signature");
            Event event = Webhook.constructEvent(payload, sigHeader, stripeSecretEndpoint);
            switch (event.getType()) {
            case "payment_intent.succeeded":
                PaymentIntent intent = (PaymentIntent) event.getDataObjectDeserializer().getObject().orElse(null);
                break;
            case "payment_intent.payment_failed":
                break;
            }
            return ResponseEntity.ok("OK");
        } catch (HttpClientErrorException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(utils.err(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(utils.err(ex.getMessage()));
        }
    }
}
