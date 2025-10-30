package apinexo.core.modules.stripe.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import apinexo.core.modules.stripe.facade.StripeFacade;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stripe")
public class StripeController {

    private final StripeFacade stripeFacade;

    @GetMapping("/webhook")
    public ResponseEntity<Object> webhook(HttpServletRequest request) {
        return stripeFacade.webhook(request);
    }
}
