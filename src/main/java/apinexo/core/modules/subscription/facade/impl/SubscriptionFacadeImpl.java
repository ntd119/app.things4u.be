package apinexo.core.modules.subscription.facade.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.databind.JsonNode;

import apinexo.common.dtos.AbstractService;
import apinexo.common.utils.ApinexoUtils;
import apinexo.core.modules.plans.entity.PlansEntity;
import apinexo.core.modules.plans.service.ApiPlansService;
import apinexo.core.modules.subscription.dto.SubscriptionChangeSubscriptionRequest;
import apinexo.core.modules.subscription.dto.SubscriptionChangeSubscriptionResponse;
import apinexo.core.modules.subscription.entity.SubscriptionEntity;
import apinexo.core.modules.subscription.facade.SubscriptionFacade;
import apinexo.core.modules.subscription.service.SubscriptionService;
import apinexo.core.modules.user.entity.UserEntity;
import apinexo.core.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SubscriptionFacadeImpl extends AbstractService implements SubscriptionFacade {

    private final ApinexoUtils utils;

    @Value("${stripe.secret.key}")
    private String stripeSecret;

    private final UserService userService;

    private final ApiPlansService apiPlansService;

    private final SubscriptionService subscriptionService;

    @Override
    public ResponseEntity<Object> changeSubscription(Jwt jwt, SubscriptionChangeSubscriptionRequest body) {
        try {
            String sub = jwt.getClaimAsString("sub");
            Optional<UserEntity> userOptional = userService.findByAuth0UserId(sub);

            if (!userOptional.isPresent()) {
                return utils.badRequest("The user does not exist");
            }
            UserEntity userEntity = userOptional.get();

            List<PlansEntity> plansEntities = apiPlansService.findByApiId(body.getApiId());
            if (CollectionUtils.isEmpty(plansEntities)) {
                return utils.badRequest("The plans does not exist");
            }

            Optional<PlansEntity> plansOptional = plansEntities.stream()
                    .filter(plan -> plan.getKey().equalsIgnoreCase(body.getPlanKey())).findFirst();
            if (!plansOptional.isPresent()) {
                return utils.badRequest("The plans does not exist");
            }
            PlansEntity plansEntity = plansOptional.get();
            if (plansEntity.getIsFree()) {
                String subscriptionId = utils.generateRandomHexString(24);
                SubscriptionEntity subscribe = SubscriptionEntity.builder().id(subscriptionId).user(userEntity)
                        .subscribedAt(LocalDateTime.now()).build();
                subscribe.setSubscribedAt(LocalDateTime.now());
                subscriptionService.save(subscribe);
                return ResponseEntity.ok("OK");
            } else {
                String priceId = plansEntities.stream()
                        .filter(plan -> plan.getKey().equalsIgnoreCase(body.getPlanKey())).map(PlansEntity::getId)
                        .findFirst().orElse(null);

                String userEmail = userEntity.getEmail();
                HttpHeaders headers = utils.buildHeader();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                headers.setBasicAuth(stripeSecret, "");
                MultiValueMap<String, String> bodyClient = new LinkedMultiValueMap<>();
                bodyClient.add("mode", "subscription");
                bodyClient.add("success_url", body.getUrl());
                bodyClient.add("cancel_url", body.getUrl());
                bodyClient.add("customer_email", userEmail);
                bodyClient.add("line_items[0][price]", priceId);
                bodyClient.add("line_items[0][quantity]", "1");
                bodyClient.add("payment_method_types[0]", "card");
                bodyClient.add("payment_method_types[1]", "link");
                String url = "https://api.stripe.com/v1/checkout/sessions";
                JsonNode response = executePostRequest(JsonNode.class, url, bodyClient, headers);
                SubscriptionChangeSubscriptionResponse subscriptionResponse = SubscriptionChangeSubscriptionResponse
                        .builder().url(utils.jsonNodeAt(response, "/url", String.class)).build();
                return ResponseEntity.ok(subscriptionResponse);
            }
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}