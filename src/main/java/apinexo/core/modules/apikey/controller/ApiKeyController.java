package apinexo.core.modules.apikey.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import apinexo.core.modules.apikey.service.ApiKeyService;

@RestController
@RequiredArgsConstructor
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    @GetMapping("/get-or-create-api-key")
    public ResponseEntity<Object> getOrCreateApiKey(@AuthenticationPrincipal Jwt jwt) {
        return apiKeyService.getOrCreateApiKey(jwt);
    }
}
