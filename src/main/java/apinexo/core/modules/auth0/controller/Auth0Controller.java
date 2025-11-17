package apinexo.core.modules.auth0.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import apinexo.core.modules.auth0.dto.Auth0ResendVerificationRequest;
import apinexo.core.modules.auth0.facade.Auth0Facade;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dev")
public class Auth0Controller {

    private final Auth0Facade auth0Facade;

    @PostMapping("/resend-verification")
    public ResponseEntity<Object> resendVerification(@RequestBody Auth0ResendVerificationRequest request) {
        return auth0Facade.resendVerification(request);
    }
}
