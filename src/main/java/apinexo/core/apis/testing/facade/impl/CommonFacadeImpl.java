package apinexo.core.apis.testing.facade.impl;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.databind.JsonNode;

import apinexo.common.dtos.AbstractService;
import apinexo.common.utils.ApinexoUtils;
import apinexo.core.apis.testing.facade.CommonFacade;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CommonFacadeImpl extends AbstractService implements CommonFacade {

    private final ApinexoUtils utils;

    public ResponseEntity<?> forwardToThirdParty(String url, String method, String query, String body) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            if (method.equals("GET")) {
                return executeGetRequest(String.class, url, headers);
            }
            if (method.equals("POST")) {
                return executePostRequest(String.class, url, utils.convertDtoToJson(body), headers);
            }
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
        } catch (HttpClientErrorException ex) {
            JsonNode error = utils.convertStrToJson(ex.getResponseBodyAsString());
            return ResponseEntity.status(ex.getStatusCode()).contentType(MediaType.APPLICATION_JSON)
                    .body(error.toString());
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(utils.err(ex.getMessage()));
        }
    }
}
