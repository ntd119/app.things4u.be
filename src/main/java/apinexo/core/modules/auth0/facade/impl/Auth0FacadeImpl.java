package apinexo.core.modules.auth0.facade.impl;

import java.util.Map;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.databind.JsonNode;

import apinexo.common.dtos.AbstractService;
import apinexo.common.utils.ApinexoUtils;
import apinexo.core.modules.auth0.dto.Auth0ResendVerificationRequest;
import apinexo.core.modules.auth0.facade.Auth0Facade;
import apinexo.core.modules.auth0.service.Auth0Service;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class Auth0FacadeImpl extends AbstractService implements Auth0Facade {

    private final ApinexoUtils utils;

    private final Auth0Service auth0Service;

    @Override
    public ResponseEntity<Object> resendVerification(Auth0ResendVerificationRequest request) {
        try {
            ResponseEntity<JsonNode> userResponse = auth0Service.getUserByEmail(request.getEmail());
            if (!userResponse.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.status(userResponse.getStatusCode()).body(userResponse.getBody());
            }
            JsonNode user = userResponse.getBody();
            if (Objects.isNull(user) || user.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
            ResponseEntity<JsonNode> response = auth0Service
                    .resendVerificationEmail(utils.jsonNodeAt(user, "/0/user_id", String.class));
            String message = "Verification email sent successfully";
            if (!response.getStatusCode().is2xxSuccessful()) {
                message = "Failed to resend verification email";
            }
            return ResponseEntity.status(response.getStatusCode()).body(Map.of("message", message));
        } catch (HttpClientErrorException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(utils.err(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(utils.err(ex.getMessage()));
        }
    }
}
