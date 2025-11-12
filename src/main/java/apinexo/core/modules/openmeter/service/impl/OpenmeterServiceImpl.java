package apinexo.core.modules.openmeter.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import apinexo.common.dtos.AbstractService;
import apinexo.common.utils.ApinexoUtils;
import apinexo.core.modules.openmeter.request.client.OpenmeterStripeCheckoutSessionsClientRequest;
import apinexo.core.modules.openmeter.request.client.OpenmeterUpsertSubjectClientRequest;
import apinexo.core.modules.openmeter.service.OpenmeterService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OpenmeterServiceImpl extends AbstractService implements OpenmeterService {

    private final ApinexoUtils utils;

    @Value("${openmeter.secret-token}")
    private String secretToken;

    @Override
    public void upsertSubject(String key, String displayName) {
        OpenmeterUpsertSubjectClientRequest clientRequest = OpenmeterUpsertSubjectClientRequest.builder().key(key)
                .displayName(displayName).build();
        HttpHeaders headers = utils.buildHeader();
        headers.setBearerAuth(secretToken);
        String url = "https://openmeter.cloud/api/v1/subjects";
        List<OpenmeterUpsertSubjectClientRequest> body = utils.createList(clientRequest);
        executePostRequest(JsonNode.class, url, body, headers);
    }

    @Override
    public JsonNode stripeCheckoutSessions() {
        OpenmeterStripeCheckoutSessionsClientRequest body = OpenmeterStripeCheckoutSessionsClientRequest.builder()
                .customer(OpenmeterStripeCheckoutSessionsClientRequest.Customer.builder()
                        .key("google-oauth2|118088024087048155774")
                        .usageAttribution(OpenmeterStripeCheckoutSessionsClientRequest.Customer.UsageAttribution
                                .builder().subjectKeys(List.of("google-oauth2|118088024087048155774")).build())
                        .build())
                .plan(OpenmeterStripeCheckoutSessionsClientRequest.Plan.builder().key("pro").build())
                .options(OpenmeterStripeCheckoutSessionsClientRequest.Options.builder()
                        .successUrl("http://localhost:3000").currency("USD").build())
                .build();
        HttpHeaders headers = utils.buildHeader();
        headers.setBearerAuth(secretToken);
        String url = "https://openmeter.cloud/api/v1/stripe/checkout/sessions";
        return executePostRequest(JsonNode.class, url, body, headers).getBody();
    }

}
