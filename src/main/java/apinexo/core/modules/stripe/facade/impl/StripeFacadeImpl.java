package apinexo.core.modules.stripe.facade.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.databind.JsonNode;

import apinexo.common.dtos.AbstractService;
import apinexo.common.utils.ApinexoUtils;
import apinexo.core.modules.stripe.facade.StripeFacade;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StripeFacadeImpl extends AbstractService implements StripeFacade {

    private final ApinexoUtils utils;

    @Value("${stripe.secret.key}")
    private String stripeSecret;

    @Override
    public ResponseEntity<Object> generateProduct() {
        try {
            HttpHeaders headers = utils.buildHeader();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.setBasicAuth(stripeSecret, "");
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
//            1. Tạo sản phẩm chính (JSearch)
//            body.add("name", "JSearch");
//            String url = "https://api.stripe.com/v1/products";
//            JsonNode response = executePostRequest(JsonNode.class, url, body, headers);
//            prod_TILPIhZeC5zW3f

//            2. Tạo price cố định $25/tháng (base subscription)
//            String url = "https://api.stripe.com/v1/prices";
//            body.add("unit_amount", "2500");
//            body.add("currency", "usd");
//            body.add("recurring[interval]", "month");
//            body.add("product", "prod_TILPIhZeC5zW3f");
//            body.add("nickname", "Base subscription (monthly)");
//            JsonNode response = executePostRequest(JsonNode.class, url, body, headers);

//            3. Tạo price theo usage (billed monthly based on usage)
//            String url = "https://api.stripe.com/v1/prices";
//            body.add("currency", "usd");
//            body.add("recurring[interval]", "month");
//            body.add("recurring[usage_type]", "metered");
//            body.add("billing_scheme", "per_unit");
//            body.add("unit_amount", "100");
//            body.add("product", "prod_TILPIhZeC5zW3f");
//            body.add("nickname", "Usage-based charges");
//            JsonNode response = executePostRequest(JsonNode.class, url, body, headers);
            
//            4. Tạo Subscription (hiển thị như hình)
            String url = "https://api.stripe.com/v1/subscriptions";
            body.add("customer", "cus_TILfB4YNrdSDcu");
            body.add("items[0][price]", "price_1SLknOR5bl4Qw3RDADr4trRk");
            body.add("items[1][price]", "price_1SLkrDR5bl4Qw3RDbsPlR0L2");
            body.add("collection_method", "charge_automatically");
            body.add("payment_behavior", "default_incomplete");
            JsonNode response = executePostRequest(JsonNode.class, url, body, headers);

            return ResponseEntity.ok(response);
        } catch (HttpClientErrorException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(utils.err(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(utils.err(ex.getMessage()));
        }
    }
}
