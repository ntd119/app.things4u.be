package apinexo.core.modules.auth0.facade;

import org.springframework.http.ResponseEntity;

import apinexo.core.modules.auth0.dto.Auth0ResendVerificationRequest;

public interface Auth0Facade {

    ResponseEntity<Object> resendVerification(Auth0ResendVerificationRequest request);
}
