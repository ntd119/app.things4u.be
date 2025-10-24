package apinexo.core.modules.subscription.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import apinexo.core.modules.subscription.facade.SubscriptionFacade;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionFacade subscriptionFacade;

    @PostMapping("/change-subscription")
    public ResponseEntity<Object> changeSubscription(@AuthenticationPrincipal Jwt jwt,
            @RequestBody Map<String, Object> body) {
        return subscriptionFacade.changeSubscription(jwt, body);
    }
}
