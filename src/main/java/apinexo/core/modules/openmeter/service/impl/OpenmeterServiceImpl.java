package apinexo.core.modules.openmeter.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import apinexo.common.dtos.AbstractService;
import apinexo.common.utils.ApinexoUtils;
import apinexo.common.utils.ConstantUtils;
import apinexo.core.modules.openmeter.dto.OpenmeterOmTokenResponse;
import apinexo.core.modules.openmeter.request.client.OpenmeterCreateCustomerClientRequest;
import apinexo.core.modules.openmeter.request.client.OpenmeterSendEventClientRequest;
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
    public JsonNode createCustomer(String id, String name, String description, String email, String subjectKeys) {
        OpenmeterCreateCustomerClientRequest body = OpenmeterCreateCustomerClientRequest.builder().name(name)
                .description(description).key(id)
                .usageAttribution(OpenmeterCreateCustomerClientRequest.UsageAttribution.builder()
                        .subjectKeys(utils.createList(subjectKeys)).build())
                .primaryEmail(email).currency("USD")
                .billingAddress(OpenmeterCreateCustomerClientRequest.BillingAddress.builder().country("US")
                        .postalCode("").state("").city("").line1("").line2("").phoneNumber("").build())
                .build();

        HttpHeaders headers = utils.buildHeader();
        headers.setBearerAuth(secretToken);
        String url = "https://openmeter.cloud/api/v1/customers";
        ResponseEntity<JsonNode> response = executePostRequest(JsonNode.class, url, body, headers);
        return response.getBody();
    }

    @Override
    public void deleteCustomer​(String customerIdOrKey) {
        HttpHeaders headers = utils.buildHeader();
        headers.setBearerAuth(secretToken);
        String url = "https://openmeter.cloud/api/v1/customers/" + customerIdOrKey;
        executeDeleteRequest(JsonNode.class, url, null, headers);
    }

    @Override
    public void deleteSubject​(String id) {
        HttpHeaders headers = utils.buildHeader();
        headers.setBearerAuth(secretToken);
        String url = "https://openmeter.cloud/api/v1/subjects/" + id;
        executeDeleteRequest(JsonNode.class, url, null, headers);
    }

    @Override
    public void events(String apiId, String subjectKeys) {
        LocalDateTime currentDate = utils.getCurrentDateTime(ConstantUtils.TIME_ZONE_UCT);
        String formatDate = utils.formatDateTime(currentDate, ConstantUtils.DateFormat.YYYYMMDDTHHMMssSSS);
        OpenmeterSendEventClientRequest body = OpenmeterSendEventClientRequest.builder().specversion("1.0")
                .type("request").id(utils.uuidRandom()).time(formatDate + "Z").source("api_requests_total")
                .subject(subjectKeys)
                .data(OpenmeterSendEventClientRequest.DataContent.builder().value("1").apiName(apiId).build()).build();
        HttpHeaders headers = utils.buildHeader();
        headers.setBearerAuth(secretToken);
        String url = "https://openmeter.cloud/api/v1/events";
        executePostRequest(OpenmeterOmTokenResponse.class, url, body, headers);
    }
}
