package apinexo.core.modules.apikey.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import apinexo.core.modules.apikey.facade.ApiKeyFacade;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dev")
public class ApiKeyController {

    private final ApiKeyFacade apiKeyFacade;

    @GetMapping("/reset-api-key")
    public ResponseEntity<Object> resetApiKey(@AuthenticationPrincipal Jwt jwt) {
        return apiKeyFacade.resetApiKey(jwt);
    }
}
