package apinexo.core.modules.stripe.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
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
import com.stripe.model.Invoice;
import com.stripe.model.InvoiceItem;
import com.stripe.model.Subscription;
import com.stripe.param.InvoiceCreateParams;
import com.stripe.param.InvoiceItemCreateParams;

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
    public ResponseEntity<Object> cancelSubscription(String subscriptionId, String customerId,
            String subscriptionItemId) throws StripeException {

        Stripe.apiKey = stripeSecret;

        // -----------------------------------------
        // 1) Tính toán số tiền usage
        // -----------------------------------------
        long billableUsage = 15000;
        long totalCents = Math.round(billableUsage * 0.003 * 100); // Stripe tính bằng cents

        if (totalCents <= 0) {
            // Không có usage để charge, chỉ hủy subscription
            Subscription sub = Subscription.retrieve(subscriptionId);
            sub.cancel();

            Map<String, Object> result = Map.of(
                    "status", "subscription_cancelled",
                    "message", "No usage to bill"
            );
            return ResponseEntity.ok(result);
        }

        // -----------------------------------------
        // 2) Tạo invoice item gắn subscription
        // -----------------------------------------
        InvoiceItemCreateParams itemParams = InvoiceItemCreateParams.builder()
                .setCustomer(customerId)
                .setSubscription(subscriptionId)
                .setCurrency("usd")
                .setAmount(totalCents)
                .setDescription("Final usage charge (" + billableUsage + " units)")
                .build();

        InvoiceItem.create(itemParams);

        // -----------------------------------------
        // 3) Tạo invoice thật + finalize
        // -----------------------------------------
        InvoiceCreateParams createParams = InvoiceCreateParams.builder()
                .setCustomer(customerId)
                .setSubscription(subscriptionId)
                .setAutoAdvance(true)
                .build();

        Invoice finalInvoice = Invoice.create(createParams);
        finalInvoice = finalInvoice.finalizeInvoice();

        // -----------------------------------------
        // 4) Hủy subscription
        // -----------------------------------------
        Subscription sub = Subscription.retrieve(subscriptionId);
        sub.cancel();

        // -----------------------------------------
        // 5) Trả về thông tin invoice
        // -----------------------------------------
        Map<String, Object> response = Map.of(
                "status", "success",
                "invoice_id", finalInvoice.getId(),
                "invoice_url", finalInvoice.getHostedInvoiceUrl(),
                "invoice_total_usd", finalInvoice.getAmountDue() / 100.0,
                "message", "Subscription cancelled and final usage invoice created."
        );

        return ResponseEntity.ok(response);
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
