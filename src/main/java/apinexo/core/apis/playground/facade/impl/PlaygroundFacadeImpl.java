package apinexo.core.apis.playground.facade.impl;

import java.net.URI;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.databind.JsonNode;

import apinexo.common.dtos.AbstractService;
import apinexo.common.utils.ApinexoUtils;
import apinexo.core.apis.playground.facade.PlaygroundFacade;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PlaygroundFacadeImpl extends AbstractService implements PlaygroundFacade {

    private final ApinexoUtils utils;

    public ResponseEntity<?> dynamicProxy(HttpServletRequest request, String body) {
        try {

            String fullPath = request.getRequestURI();
            String method = request.getMethod();

            String prefix = null;
            URI uri = new URI(fullPath);
            String path = uri.getPath();
            String[] parts = path.split("/");
            if (parts.length > 1) {
                prefix = parts[1];
            }

            JsonNode apis = utils.readJsonFile("/data_static/api-config.json", JsonNode.class);
            JsonNode apiItem = utils.getJsonInList(apis, "id", prefix);
            if (apiItem == null || apiItem.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_JSON)
                        .body(Map.of("message", "API config not found for: " + fullPath));
            }
            JsonNode urls = utils.jsonNodeAt(apiItem, "/urls");
            int index = utils.getRandom().nextInt(urls.size());
            JsonNode randomItem = urls.get(index);
            String baseUrl = randomItem.asText();
            String forwardPath = fullPath.replace("/" + prefix, "");
            String finalUrl = baseUrl + forwardPath;
            String query = request.getParameterMap().entrySet().stream()
                    .flatMap(e -> Arrays.stream(e.getValue()).map(v -> "%s=%s".formatted(e.getKey(), v)))
                    .collect(Collectors.joining("&"));
            if (StringUtils.isNotBlank(query)) {
                finalUrl += "?" + query;
            }

            HttpHeaders headers = new HttpHeaders();
            String secretHeader = utils.jsonNodeAt(apiItem, "/secret-header", String.class);
            if (StringUtils.isNotBlank(secretHeader)) {
                headers.set("X-RapidAPI-Proxy-Secret", secretHeader);
            }
            headers.setContentType(MediaType.APPLICATION_JSON);
            return this.forwardToThirdParty(headers, finalUrl, method, query, body);

        } catch (HttpClientErrorException ex) {
            JsonNode error = utils.convertStrToJson(ex.getResponseBodyAsString());
            return ResponseEntity.status(ex.getStatusCode()).contentType(MediaType.APPLICATION_JSON)
                    .body((Objects.nonNull(error) && !error.isEmpty()) ? error.toString() : ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(utils.err(ex.getMessage()));
        }
    }

    public ResponseEntity<?> forwardToThirdParty(HttpHeaders headers, String url, String method, String query,
            String body) {
        try {

            if (method.equals("GET")) {
                ResponseEntity<String> resq = executeGetRequest(String.class, url, headers);
                return ResponseEntity.status(resq.getStatusCode()).contentType(MediaType.APPLICATION_JSON)
                        .body(resq.getBody());
            }
            if (method.equals("POST")) {
                ResponseEntity<String> resq = executePostRequest(String.class, url, body, headers);
                return ResponseEntity.status(resq.getStatusCode()).contentType(MediaType.APPLICATION_JSON)
                        .body(resq.getBody());
            }
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
        } catch (HttpClientErrorException ex) {
            JsonNode error = utils.convertStrToJson(ex.getResponseBodyAsString());
            return ResponseEntity.status(ex.getStatusCode()).contentType(MediaType.APPLICATION_JSON)
                    .body((Objects.nonNull(error) && !error.isEmpty()) ? error.toString() : ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(utils.err(ex.getMessage()));
        }
    }
}
