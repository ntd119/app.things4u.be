package apinexo.core.modules.auth0.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/change-password")
    public ResponseEntity<Object> changePassword(@AuthenticationPrincipal Jwt jwt) {
        return auth0Facade.changePassword(jwt);
    }
}
