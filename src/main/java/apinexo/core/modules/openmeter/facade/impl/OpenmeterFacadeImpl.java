package apinexo.core.modules.openmeter.facade.impl;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.databind.JsonNode;

import apinexo.client.exception.ApiException;
import apinexo.common.dtos.AbstractService;
import apinexo.common.utils.ApinexoUtils;
import apinexo.common.utils.ConstantUtils;
import apinexo.core.modules.openmeter.dto.OpenmeterOmTokenResponse;
import apinexo.core.modules.openmeter.facade.OpenmeterFacade;
import apinexo.core.modules.openmeter.request.client.OpenmeterSendEventClientRequest;
import apinexo.core.modules.openmeter.request.client.OpenmeterTokenClientRequest;
import apinexo.core.modules.user.entity.UserEntity;
import apinexo.core.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OpenmeterFacadeImpl extends AbstractService implements OpenmeterFacade {

    private final ApinexoUtils utils;

    private final UserService userService;

    @Value("${openmeter.secret-token}")
    private String secretToken;

    private static final String PATH_FILE = "data_static/";

    @Override
    public ResponseEntity<Object> omToken(Jwt jwt) {
        try {
            String sub = jwt.getClaimAsString("sub");
            Optional<UserEntity> user = userService.findByAuth0UserId(sub);
            if (!user.isPresent()) {
                return ResponseEntity.badRequest().body(new ApiException("The user does not exist"));
            }

            OpenmeterTokenClientRequest body = OpenmeterTokenClientRequest.builder().subject(user.get().getId())
                    .allowedMeterSlugs(utils.createList("api_requests_total")).build();

            HttpHeaders headers = utils.buildHeader();
            headers.setBearerAuth(secretToken);
            String url = "https://openmeter.cloud/api/v1/portal/tokens";
            OpenmeterOmTokenResponse response = executePostRequest(OpenmeterOmTokenResponse.class, url, body, headers);
            return ResponseEntity.ok(response);
        } catch (HttpClientErrorException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(utils.err(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(utils.err(ex.getMessage()));
        }
    }

    @Override
    public ResponseEntity<Object> sendEvent(Jwt jwt) {
        try {
            String sub = jwt.getClaimAsString("sub");
            Optional<UserEntity> user = userService.findByAuth0UserId(sub);
            if (!user.isPresent()) {
                return ResponseEntity.badRequest().body(new ApiException("The user does not exist"));
            }
            LocalDateTime currentDate = utils.getCurrentDateTime(ConstantUtils.TIME_ZONE_UCT);
            String formatDate = utils.formatDateTime(currentDate, ConstantUtils.DateFormat.YYYYMMDDTHHMMssSSS);

            OpenmeterSendEventClientRequest body = OpenmeterSendEventClientRequest.builder().specversion("1.0")
                    .type("request").id(utils.uuidRandom()).time(formatDate + "Z").source("api_requests_total")
                    .subject(user.get().getId())
                    .data(OpenmeterSendEventClientRequest.DataContent.builder().value("1").apiName("jsearch").build())
                    .build();
            HttpHeaders headers = utils.buildHeader();
            headers.setBearerAuth(secretToken);
            String url = "https://openmeter.cloud/api/v1/events";
            OpenmeterOmTokenResponse response = executePostRequest(OpenmeterOmTokenResponse.class, url, body, headers);
            return ResponseEntity.ok(response);
        } catch (HttpClientErrorException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(utils.err(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(utils.err(ex.getMessage()));
        }
    }

    @Override
    public ResponseEntity<Object> generate() {
        try {
            // generate meters api_requests
            HttpHeaders headers = utils.buildHeader();
            headers.setBearerAuth(secretToken);
            String url = "https://openmeter.cloud/api/v1/meters";
            JsonNode body = utils.readJsonFile(PATH_FILE + "meters_api_requests_per_month.json", JsonNode.class);
//            executePostRequest(OpenmeterOmTokenResponse.class, url, body, headers);

            // generate meters meters_requests_per_hour.json
//            body = utils.readJsonFile(PATH_FILE + "meters_requests_per_hour.json", JsonNode.class);
//            executePostRequest(OpenmeterOmTokenResponse.class, url, body, headers);

            // generate meters meters_requests_per_second.json
//            body = utils.readJsonFile(PATH_FILE + "meters_requests_per_second.json", JsonNode.class);
//            executePostRequest(OpenmeterOmTokenResponse.class, url, body, headers);

            // generate feature requests_per_month.json
//            url = "https://openmeter.cloud/api/v1/features";
//            body = utils.readJsonFile(PATH_FILE + "feature_requests_per_month.json", JsonNode.class);
//            executePostRequest(OpenmeterOmTokenResponse.class, url, body, headers);

            // generate feature api_requests_per_hour
//            url = "https://openmeter.cloud/api/v1/features";
//            body = utils.readJsonFile(PATH_FILE + "feature_api_requests_per_hour.json", JsonNode.class);
//            executePostRequest(OpenmeterOmTokenResponse.class, url, body, headers);

            // generate feature api_requests_per_second
//            url = "https://openmeter.cloud/api/v1/features";
//            body = utils.readJsonFile(PATH_FILE + "feature_api_requests_per_second.json", JsonNode.class);
//            executePostRequest(OpenmeterOmTokenResponse.class, url, body, headers);

            // generate plan basic
//            url = "https://openmeter.cloud/api/v1/plans";
//            body = utils.readJsonFile(PATH_FILE + "plan_basic.json", JsonNode.class);
//            executePostRequest(OpenmeterOmTokenResponse.class, url, body, headers);

            // generate plan pro
//            url = "https://openmeter.cloud/api/v1/plans";
//            body = utils.readJsonFile(PATH_FILE + "plan_pro.json", JsonNode.class);
//            executePostRequest(OpenmeterOmTokenResponse.class, url, body, headers);

            // generate plan ultra
//            url = "https://openmeter.cloud/api/v1/plans";
//            body = utils.readJsonFile(PATH_FILE + "plan_ultra.json", JsonNode.class);
//            executePostRequest(OpenmeterOmTokenResponse.class, url, body, headers);

            // generate plan mega
            url = "https://openmeter.cloud/api/v1/plans";
            body = utils.readJsonFile(PATH_FILE + "plan_mega.json", JsonNode.class);
            executePostRequest(OpenmeterOmTokenResponse.class, url, body, headers);

            return ResponseEntity.ok("OK");
        } catch (HttpClientErrorException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(utils.convertStrToJson(ex.getResponseBodyAsString()));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(utils.err(ex.getMessage()));
        }
    }

}
