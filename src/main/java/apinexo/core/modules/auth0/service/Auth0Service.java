package apinexo.core.modules.auth0.service;

import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.JsonNode;

public interface Auth0Service {

    ResponseEntity<JsonNode> getUser(String sub);

    ResponseEntity<JsonNode> getUserByEmail(String email);

    ResponseEntity<JsonNode> resendVerificationEmail(String userId);

    ResponseEntity<JsonNode> generateToken();
}
