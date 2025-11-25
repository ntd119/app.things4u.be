package apinexo.core.modules.stripe.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import apinexo.core.modules.stripe.facade.StripeFacade;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dev")
public class StripeController {

    private final StripeFacade stripeFacade;

    @PostMapping("/stripe/webhook")
    public ResponseEntity<Object> webhook(HttpServletRequest request) {
        return stripeFacade.webhook(request);
    }

    @GetMapping("/create-portal-session")
    public ResponseEntity<Object> createPortalSession(@AuthenticationPrincipal Jwt jwt) {
        return stripeFacade.createPortalSession(jwt);
    }
}
