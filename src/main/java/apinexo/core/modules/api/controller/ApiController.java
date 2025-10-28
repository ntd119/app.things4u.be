package apinexo.core.modules.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import apinexo.core.modules.api.facade.ApiFacade;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ApiController {

    private final ApiFacade apiFacade;

    @GetMapping("/list-apis")
    public ResponseEntity<Object> listApis(@AuthenticationPrincipal Jwt jwt) {
        return apiFacade.listApis(jwt);
    }
}
