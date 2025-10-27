package apinexo.core.modules.subscription.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import apinexo.core.modules.subscription.dto.SubscriptionChangeSubscriptionRequest;
import apinexo.core.modules.subscription.facade.SubscriptionFacade;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionFacade subscriptionFacade;

    @PostMapping("/change-subscription")
    public ResponseEntity<Object> changeSubscription(@AuthenticationPrincipal Jwt jwt,
            @RequestBody SubscriptionChangeSubscriptionRequest body) {
        return subscriptionFacade.changeSubscription(jwt, body);
    }

    @GetMapping("/get-subscriptions")
    public ResponseEntity<Object> getSubscriptions(@AuthenticationPrincipal Jwt jwt) {
        return subscriptionFacade.getSubscriptions(jwt);
    }
}
