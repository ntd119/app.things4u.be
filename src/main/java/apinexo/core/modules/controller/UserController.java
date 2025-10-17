package apinexo.core.modules.controller;

import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.oauth2.jwt.Jwt;

@RestController
@RequestMapping("/api")
public class UserController {
    
    @GetMapping("/public/hello")
    public String publicHello() {
        return "Public API - no token required";
    }

    @GetMapping("/user")
    public Map<String, Object> getUser(@AuthenticationPrincipal Jwt jwt) {
        return Map.of(
            "sub", jwt.getSubject(),
            "email", jwt.getClaim("email"),
            "scope", jwt.getClaim("scope"),
            "issuer", jwt.getIssuer().toString()
        );
    }

}
