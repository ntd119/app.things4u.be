package apinexo.core.modules.stripe.service.impl;

import java.math.BigDecimal;
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
import apinexo.core.modules.subscription.entity.SubscriptionEntity;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StripeServiceImpl extends AbstractService implements StripeService {

    private final ApinexoUtils utils;

    @Value("${stripe.secret.key}")
    private String stripeSecret;

    @Override
    public JsonNode createPriceSoftLimit(String apiName, String upTo, String price) {
        BigDecimal unitAmount = new BigDecimal(price).multiply(new BigDecimal("100"));
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
    public ResponseEntity<Object> cancelSubscription(SubscriptionEntity subscription) throws StripeException {
        Stripe.apiKey = stripeSecret;
        String subscriptionId = subscription.getId();
//        boolean isSoftLimit = subscription.getIsSoftLimit();
//        if (isSoftLimit) {
//            long quota = subscription.getQuota();
//            long currentUsage = this.getCurrentUsage(subscription.getSubscriptionItemId());
//            long billableUsage = currentUsage - quota;
//            if (billableUsage > 0) {
//                double overagePrices = subscription.getOveragePrices();
//                long totalCents = Math.round(billableUsage * overagePrices * 100);
//                String customerId = subscription.getUser().getStripeCustomerId();
//                String description = String.format("%s %s (%d × $%.2f / request)", subscription.getApi().getName(),
//                        subscription.getCurrentPlan(), billableUsage, overagePrices);
//                InvoiceItemCreateParams itemParams = InvoiceItemCreateParams.builder().setCustomer(customerId)
//                        .setSubscription(subscriptionId).setCurrency("usd").setAmount(totalCents)
//                        .setDescription(description).build();
//                InvoiceItem.create(itemParams);
//                InvoiceCreateParams createParams = InvoiceCreateParams.builder().setCustomer(customerId)
//                        .setSubscription(subscriptionId).setAutoAdvance(true).build();
//                Invoice finalInvoice = Invoice.create(createParams);
//                finalInvoice = finalInvoice.finalizeInvoice();
//                finalInvoice.pay();
//            }
//        }
        Subscription sub = Subscription.retrieve(subscriptionId);
        sub = sub.cancel();
        return ResponseEntity.ok(sub);
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

    @Override
    public long getCurrentUsage(String subscriptionItemId) {
        HttpHeaders headers = utils.buildHeader();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(stripeSecret, "");
        String url = "https://api.stripe.com/v1/subscription_items/" + subscriptionItemId + "/usage_record_summaries";
        ResponseEntity<String> response = executeGetRequest(String.class, url, headers);
        JsonNode json = utils.convertStrToJson(response.getBody());
        return utils.jsonNodeAt(json, "/data/0/total_usage", Long.class);
    }
}
