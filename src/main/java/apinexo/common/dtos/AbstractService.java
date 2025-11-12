package apinexo.common.dtos;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;

public abstract class AbstractService {

    @Autowired
    private RestTemplate restTemplate;

    protected <T, R> ResponseEntity<T> executeGetRequest(Class<T> clazz, final String endpoint,
            final HttpHeaders headers) {
        return executeRequest(clazz, endpoint, null, HttpMethod.GET, headers, true);
    }

    protected <T, R> ResponseEntity<T> executeGetRequest(Class<T> clazz, final String endpoint, final R request,
            final HttpHeaders headers) {
        return executeRequest(clazz, endpoint, request, HttpMethod.GET, headers, true);
    }

    protected <T, R> ResponseEntity<T> executePostRequest(Class<T> clazz, final String endpoint, final R request,
            final HttpHeaders headers) {
        return executeRequest(clazz, endpoint, request, HttpMethod.POST, headers, true);
    }

    protected <T, R> ResponseEntity<T> executePutRequest(Class<T> clazz, final String endpoint, final R request,
            final HttpHeaders headers) {
        return executeRequest(clazz, endpoint, request, HttpMethod.PATCH, headers, true);
    }

    protected <T, R> ResponseEntity<T> executeDeleteRequest(Class<T> clazz, final String endpoint, final R request,
            final HttpHeaders headers) {
        return executeRequest(clazz, endpoint, request, HttpMethod.DELETE, headers, true);
    }

    private <T, R> ResponseEntity<T> executeRequest(Class<T> clazz, final String endpoint, final R requestBody,
            final HttpMethod httpMethod, final HttpHeaders headers, boolean recall) {
        ResponseEntity<T> response;
        HttpEntity<?> entity;
        if (Objects.nonNull(requestBody)) {
            entity = new HttpEntity<>(requestBody, headers);
        } else {
            entity = new HttpEntity<>(headers);
        }
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        response = restTemplate.exchange(endpoint, httpMethod, entity, clazz);
        stopWatch.stop();
        return response;
    }
}
