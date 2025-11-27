package apinexo.core.modules.auth0.facade;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;

import apinexo.core.modules.auth0.dto.Auth0ResendVerificationRequest;

public interface Auth0Facade {

    ResponseEntity<Object> resendVerification(Auth0ResendVerificationRequest request);

    ResponseEntity<Object> changePassword(Jwt jwt);
}
