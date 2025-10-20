package apinexo.core.modules.openmeter.facade.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import apinexo.client.exception.ApiException;
import apinexo.common.dtos.AbstractService;
import apinexo.common.utils.ApinexoUtils;
import apinexo.core.modules.openmeter.dto.OpenmeterOmTokenResponse;
import apinexo.core.modules.openmeter.facade.OpenmeterFacade;
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

    @Override
    public ResponseEntity<Object> omToken(Jwt jwt) {
        try {
            String sub = jwt.getClaimAsString("sub");
            Optional<UserEntity> user = userService.findByAuth0UserId(sub);
            if (!user.isPresent()) {
                return ResponseEntity.badRequest().body(new ApiException("The user does not exist"));
            }

            OpenmeterTokenClientRequest body = OpenmeterTokenClientRequest.builder().subject(user.get().getUserId())
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
}
