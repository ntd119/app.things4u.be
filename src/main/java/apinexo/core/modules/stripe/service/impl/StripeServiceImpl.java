package apinexo.core.modules.stripe.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.databind.JsonNode;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Subscription;

import apinexo.common.dtos.AbstractService;
import apinexo.common.utils.ApinexoUtils;
import apinexo.core.modules.stripe.service.StripeService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StripeServiceImpl extends AbstractService implements StripeService {

    private final ApinexoUtils utils;

    @Value("${stripe.secret.key}")
    private String stripeSecret;

    @Override
    public JsonNode createPriceSoftLimit(String apiName, String upTo, String price) {
        BigDecimal unitAmount = new BigDecimal(price).multiply(new BigDecimal("100")).setScale(2, RoundingMode.HALF_UP);
        HttpHeaders headers = utils.buildHeader();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(stripeSecret, "");
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("currency", "usd");
        body.add("recurring[interval]", "month");
        body.add("recurring[usage_type]", "metered");
        body.add("billing_scheme", "tiered");
        body.add("tiers_mode", "graduated");
        body.add("product_data[name]", apiName);
        body.add("tiers[0][up_to]", upTo);
        body.add("tiers[0][unit_amount]", "0");
        body.add("tiers[1][up_to]", "inf");
        body.add("tiers[1][unit_amount_decimal]", unitAmount.toPlainString());
        String url = "https://api.stripe.com/v1/prices";
        ResponseEntity<String> response = executePostRequest(String.class, url, body, headers);
        return utils.convertStrToJson(response.getBody());
    }

    @Override
    public JsonNode createPriceHardLimit(MultiValueMap<String, Object> body) {
        HttpHeaders headers = utils.buildHeader();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(stripeSecret, "");
        if (Objects.isNull(body)) {
            body = new LinkedMultiValueMap<>();
            body.add("unit_amount", "2500");
            body.add("product_data[name]", "JSearch");
            body.add("nickname", "Pro Base Plan");
            body.add("metadata[api_id]", "jsearch");
            body.add("metadata[key]", "pro");
            body.add("metadata[is_soft_limit]", "false");
            body.add("metadata[rate_limit]", "5");
            body.add("metadata[rate_limit_period]", "second");
        }
        body.add("currency", "usd");
        body.add("recurring[interval]", "month");
        String url = "https://api.stripe.com/v1/prices";
        ResponseEntity<String> response = executePostRequest(String.class, url, body, headers);
        return utils.convertStrToJson(response.getBody());
    }

    @Override
    public ResponseEntity<Object> cancelSubscription(String subscriptionId) throws StripeException {
        Stripe.apiKey = stripeSecret;
        Subscription sub = Subscription.retrieve(subscriptionId);
        Map<String, Object> params = new HashMap<>();
        params.put("invoice_now", true);
        params.put("prorate", true);
        params.put("cancellation_details", Map.of("comment", "User cancelled from backend"));
        Subscription cancelled = sub.cancel(params);
        return ResponseEntity.ok(cancelled);
    }

    @Override
    public ResponseEntity<Object> reportUsage(String subscriptionItemId, long quantity) {
        HttpHeaders headers = utils.buildHeader();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(stripeSecret, "");

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("quantity", String.valueOf(quantity));
        body.add("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        body.add("action", "increment");

        String url = "https://api.stripe.com/v1/subscription_items/" + subscriptionItemId + "/usage_records";
        ResponseEntity<String> response = executePostRequest(String.class, url, body, headers);
        JsonNode json = utils.convertStrToJson(response.getBody());
        return ResponseEntity.status(response.getStatusCode()).body(json);
    }
}
