package apinexo.common.dtos;

import java.util.Objects;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import apinexo.client.exception.ApiException;

public abstract class AbstractService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractService.class);

    private RestTemplate restTemplate;


    protected <T, R> T executeGetRequest(Class<T> clazz, final String endpoint, final HttpHeaders headers) {
        return executeRequest(clazz, endpoint, null, HttpMethod.GET, headers, true);
    }

    protected <T, R> T executeGetRequest(Class<T> clazz, final String endpoint, final R request,
            final HttpHeaders headers) {
        return executeRequest(clazz, endpoint, request, HttpMethod.GET, headers, true);
    }

    protected <T, R> T executePostRequest(Class<T> clazz, final String endpoint, final R request,
            final HttpHeaders headers) {
        return executeRequest(clazz, endpoint, request, HttpMethod.POST, headers, true);
    }

    protected <T, R> T executePutRequest(Class<T> clazz, final String endpoint, final R request,
            final HttpHeaders headers) {
        return executeRequest(clazz, endpoint, request, HttpMethod.PATCH, headers, true);
    }

    protected <T, R> T executeDeleteRequest(Class<T> clazz, final String endpoint, final R request,
            final HttpHeaders headers) {
        return executeRequest(clazz, endpoint, request, HttpMethod.DELETE, headers, true);
    }

    private <T, R> T executeRequest(Class<T> clazz, final String endpoint, final R requestBody,
            final HttpMethod httpMethod, final HttpHeaders headers, boolean recall) {
        ResponseEntity<T> response;
        try {
            HttpEntity<?> entity;
            if (Objects.nonNull(requestBody)) {
                entity = new HttpEntity<>(requestBody, headers);
            } else {
                entity = new HttpEntity<>(headers);
            }
            logRequest(httpMethod.name(), endpoint, requestBody);

            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            response = restTemplate.exchange(endpoint, httpMethod, entity, clazz);
            stopWatch.stop();
            LOGGER.info("Rest client request nano times: {}", stopWatch.getTotalTimeSeconds());

        } catch (HttpClientErrorException ex) {
            LOGGER.error("HttpClientErrorException thrown when an HTTP 4xx is received.:::{}",
                    ExceptionUtils.getStackTrace(ex));
            throw new ApiException("Can not communication to third party");
        } catch (Exception ex) {
            LOGGER.error(ExceptionUtils.getStackTrace(ex));
            throw new ApiException("Can not communication to third party");
        }
        return response.getBody();
    }

    private void logRequest(final Object method, final Object uri, final Object payload) {
        LOGGER.info("{} {} {}", method, uri, payload);
    }

}
