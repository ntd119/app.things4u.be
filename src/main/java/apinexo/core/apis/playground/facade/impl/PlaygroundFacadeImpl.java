package apinexo.core.apis.playground.facade.impl;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;

import apinexo.common.configuration.ApiConfigCache;
import apinexo.common.dtos.AbstractService;
import apinexo.common.utils.ApinexoUtils;
import apinexo.core.apis.playground.facade.PlaygroundFacade;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class PlaygroundFacadeImpl extends AbstractService implements PlaygroundFacade {

    private final ApinexoUtils utils;

    private final ApiConfigCache apiConfigCache;

    private final WebClient webClient;

    public Mono<ResponseEntity<String>> dynamicProxy(ServerHttpRequest request, String body) {

        String fullPath = request.getURI().getPath();
        String method = request.getMethod().name();

        // prefix
        String[] parts = fullPath.split("/");
        String prefix = parts.length > 1 ? parts[1] : null;

        JsonNode apiItem = apiConfigCache.getApiByPrefix(prefix);
        if (apiItem == null || apiItem.isEmpty()) {
            return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_JSON)
                    .body("{\"message\":\"API config not found for: " + fullPath + "\"}"));
        }

        // random base url
        JsonNode urls = utils.jsonNodeAt(apiItem, "/urls");
        int index = ThreadLocalRandom.current().nextInt(urls.size());
        String baseUrl = urls.get(index).asText();

        String forwardPath = fullPath.replace("/" + prefix, "");
        String finalUrl = baseUrl + forwardPath;

        // query params (NON-BLOCKING)
        if (!request.getQueryParams().isEmpty()) {
            finalUrl += "?" + request.getQueryParams().toSingleValueMap().entrySet().stream()
                    .map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.joining("&"));
        }

        // headers
        HttpHeaders headers = new HttpHeaders();
        String secretHeader = utils.jsonNodeAt(apiItem, "/secret-header", String.class);
        if (StringUtils.isNotBlank(secretHeader)) {
            headers.set("X-RapidAPI-Proxy-Secret", secretHeader);
        }
        headers.setContentType(MediaType.APPLICATION_JSON);
        return this.forwardToThirdParty(headers, finalUrl, method, body);
    }

    public Mono<ResponseEntity<String>> forwardToThirdParty(HttpHeaders headers, String url, String method,
            String body) {
        return webClient.method(HttpMethod.valueOf(method)).uri(url).headers(h -> h.addAll(headers))
                .bodyValue(body == null ? "" : body)
                .exchangeToMono(clientResponse -> clientResponse.bodyToMono(String.class).defaultIfEmpty("")
                        .map(responseBody -> ResponseEntity.status(clientResponse.statusCode())
                                .headers(clientResponse.headers().asHttpHeaders()).body(responseBody)));
    }
}
