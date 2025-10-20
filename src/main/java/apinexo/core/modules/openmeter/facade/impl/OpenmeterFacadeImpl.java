package apinexo.core.modules.openmeter.facade.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.databind.JsonNode;

import apinexo.common.dtos.AbstractService;
import apinexo.common.utils.ApinexoUtils;
import apinexo.core.modules.openmeter.facade.OpenmeterFacade;
import apinexo.core.modules.openmeter.request.client.OpenmeterTokenClientRequest;

@Component
public class OpenmeterFacadeImpl extends AbstractService implements OpenmeterFacade {

    @Autowired
    private ApinexoUtils utils;

    @Value("${openmeter.secret-token}")
    private String secretToken;

    @Override
    public ResponseEntity<Object> omToken() {
        try {
            OpenmeterTokenClientRequest body = OpenmeterTokenClientRequest.builder().subject("customer-1")
                    .allowedMeterSlugs(utils.createList("api_requests_total")).build();

            HttpHeaders headers = utils.buildHeader();
            headers.setBearerAuth(secretToken);
            String url = "https://openmeter.cloud/api/v1/portal/tokens";
            JsonNode response = executePostRequest(JsonNode.class, url, body, headers);
            return ResponseEntity.ok(utils.ok(response));
        } catch (HttpClientErrorException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(utils.err(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(utils.err(ex.getMessage()));
        }
    }

}
