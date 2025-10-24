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
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("name", "JSearch");
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.setBasicAuth(stripeSecret, "");
            String url = "https://api.stripe.com/v1/products";
            JsonNode response = executePostRequest(JsonNode.class, url, body, headers);
            return ResponseEntity.ok(response);
        } catch (HttpClientErrorException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(utils.err(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(utils.err(ex.getMessage()));
        }
    }
}
