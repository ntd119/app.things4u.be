package apinexo.core.modules.plans.facade.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.databind.JsonNode;

import apinexo.common.dtos.AbstractService;
import apinexo.common.utils.ApinexoUtils;
import apinexo.core.modules.plans.facade.PlansFacade;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PlansFacadeImpl extends AbstractService implements PlansFacade {

    private final ApinexoUtils utils;

    @Value("${stripe.secret.key}")
    private String stripeSecret;

    @Override
    public ResponseEntity<Object> plans(Jwt jwt, String id) {
        try {

            HttpHeaders headers = utils.buildHeader();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.setBasicAuth(stripeSecret, "");
//            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
//            body.add("currency", "usd");
//            body.add("recurring[interval]", "month");
//            body.add("recurring[usage_type]", "metered");
//            body.add("billing_scheme", "tiered");
//            body.add("tiers_mode", "graduated");
//            body.add("product_data[name]", "JSearch");
//            body.add("nickname", "Pro");
//            body.add("tiers[0][up_to]", "10000");
//            body.add("tiers[0][unit_amount]", "0");
//            body.add("tiers[1][up_to]", "inf");
//            body.add("tiers[1][unit_amount_decimal]", "0.003");
//            body.add("metadata[api_id]", "jsearch");
//            body.add("metadata[key]", "pro");
//            body.add("metadata[is_soft_limit]", "true");
//            body.add("metadata[rate_limit]", "5");
//            body.add("metadata[rate_limit_period]", "second");
            String body =
                    "currency=usd" +
                    "&recurring[interval]=month" +
                    "&recurring[usage_type]=metered" +
                    "&billing_scheme=tiered" +
                    "&tiers_mode=graduated" +
                    "&product_data[name]=JSearch" +
                    "&nickname=Pro" +
                    "&tiers[0][up_to]=10000" +
                    "&tiers[0][unit_amount]=0" +
                    "&tiers[1][up_to]=inf" +
                    "&tiers[1][unit_amount_decimal]=0.003" +
                    "&metadata[api_id]=jsearch" +
                    "&metadata[key]=pro" +
                    "&metadata[is_soft_limit]=true" +
                    "&metadata[rate_limit]=5" +
                    "&metadata[rate_limit_period]=second";
            String url = "https://api.stripe.com/v1/prices";
            JsonNode response = executePostRequest(JsonNode.class, url, body, headers);

            return ResponseEntity.ok(response);
        } catch (HttpClientErrorException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(utils.err(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(utils.err(ex.getMessage()));
        }
    }
}
