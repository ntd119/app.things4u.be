package apinexo.core.modules.subscription.facade.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.databind.JsonNode;

import apinexo.common.dtos.AbstractService;
import apinexo.common.utils.ApinexoUtils;
import apinexo.core.modules.subscription.facade.SubscriptionFacade;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SubscriptionFacadeImpl extends AbstractService implements SubscriptionFacade {

    private final ApinexoUtils utils;

    @Value("${stripe.secret.key}")
    private String stripeSecret;

    @Override
    public ResponseEntity<Object> changeSubscription(Jwt jwt, Map<String, Object> body) {
        try {
            String userEmail = "marketing202001@gmail.com";
            HttpHeaders headers = utils.buildHeader();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.setBasicAuth(stripeSecret, "");
            MultiValueMap<String, String> bodyClient = new LinkedMultiValueMap<>();
            bodyClient.add("mode", "subscription");
            bodyClient.add("success_url", "http://localhost:3000/success?session_id={CHECKOUT_SESSION_ID}");
            bodyClient.add("cancel_url", "http://localhost:3000/cancel");
            bodyClient.add("customer_email", userEmail);
            bodyClient.add("line_items[0][price]", "price_1SMIIdR5bl4Qw3RDB8VkfWj5");
            bodyClient.add("line_items[0][quantity]", "1");
            bodyClient.add("payment_method_types[0]", "card");
            bodyClient.add("payment_method_types[1]", "link");
            String url = "https://api.stripe.com/v1/checkout/sessions";
            JsonNode response = executePostRequest(JsonNode.class, url, bodyClient, headers);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}