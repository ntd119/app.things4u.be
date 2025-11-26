package apinexo.core.modules.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import apinexo.core.modules.user.facade.UserFacade;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dev")
public class UserController {

    private final UserFacade facade;

    @GetMapping("/get-user")
    public ResponseEntity<Object> getUser(@AuthenticationPrincipal Jwt jwt) {
        return facade.getUser(jwt);
    }

    @GetMapping("/reset-api-key")
    public ResponseEntity<Object> resetApiKey(@AuthenticationPrincipal Jwt jwt) {
        return facade.resetApiKey(jwt);
    }
}
