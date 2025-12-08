package apinexo.core.modules.subscription.facade.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.databind.JsonNode;
import com.stripe.exception.StripeException;

import apinexo.common.dtos.AbstractService;
import apinexo.common.utils.ApinexoUtils;
import apinexo.core.modules.api.entity.ApiEntity;
import apinexo.core.modules.api.service.ApiService;
import apinexo.core.modules.logs.service.LogService;
import apinexo.core.modules.plans.converter.PlansConverter;
import apinexo.core.modules.plans.dto.ApiPlansResponse;
import apinexo.core.modules.plans.entity.PlansEntity;
import apinexo.core.modules.softlimit.entity.SoftLimitEntity;
import apinexo.core.modules.softlimit.service.SoftLimitService;
import apinexo.core.modules.stripe.service.StripeService;
import apinexo.core.modules.subscription.converter.SubscriptionConverter;
import apinexo.core.modules.subscription.dto.SubscriptionChangeSubscriptionFreeResponse;
import apinexo.core.modules.subscription.dto.SubscriptionChangeSubscriptionRequest;
import apinexo.core.modules.subscription.dto.SubscriptionChangeSubscriptionResponse;
import apinexo.core.modules.subscription.dto.SubscriptionGetQuotaUsedResponse;
import apinexo.core.modules.subscription.dto.SubscriptionResponse;
import apinexo.core.modules.subscription.entity.SubscriptionEntity;
import apinexo.core.modules.subscription.facade.SubscriptionFacade;
import apinexo.core.modules.subscription.service.SubscriptionService;
import apinexo.core.modules.user.entity.UserEntity;
import apinexo.core.modules.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SubscriptionFacadeImpl extends AbstractService implements SubscriptionFacade {

    private final ApinexoUtils utils;

    @Value("${stripe.secret.key}")
    private String stripeSecret;

    private final UserService userService;

    private final ApiService apiService;

    private final SubscriptionService subscriptionService;

    private final SoftLimitService limitService;

    private final StripeService stripeService;

    private final SoftLimitService softLimitService;

    private final PlansConverter plansConverter;

    private final SubscriptionConverter subscriptionConverter;

    @Autowired
    private LogService logService;

    @Override
    @Transactional
    public ResponseEntity<Object> changeSubscription(Jwt jwt, SubscriptionChangeSubscriptionRequest body) {
        try {
            String email = jwt.getClaimAsString("email");
            Optional<UserEntity> userOptional = userService.findByEmail(email);

            if (!userOptional.isPresent()) {
                return utils.badRequest("The user does not exist");
            }
            UserEntity userEntity = userOptional.get();

            Optional<ApiEntity> apiOptional = apiService.findbyId(body.getApiId());
            if (!apiOptional.isPresent()) {
                return utils.badRequest("The api does not exist");
            }
            ApiEntity apiEntity = apiOptional.get();

            List<PlansEntity> plansEntities = apiEntity.getPlans();
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
                SubscriptionEntity entity = subscriptionService
                        .findByUserIdAndApiId(userEntity.getId(), apiEntity.getId()).map(sub -> {
                            sub.setActive(true);
                            return sub;
                        }).orElseGet(() -> {
                            String subscriptionId = utils.generateRandomHexString(24);
                            return subscriptionService.save(subscriptionId, userEntity, apiEntity, plansEntity, null,
                                    null);
                        });
                ApiPlansResponse plans = plansConverter.entity2Resposne(entity.getPlan());
                SubscriptionChangeSubscriptionFreeResponse response = SubscriptionChangeSubscriptionFreeResponse
                        .builder().id(entity.getId()).plan(plans).build();
                return ResponseEntity.ok(response);
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
                bodyClient.add("line_items[0][price]", priceId);
                bodyClient.add("line_items[0][quantity]", "1");
                bodyClient.add("payment_method_types[0]", "card");
                bodyClient.add("payment_method_types[1]", "link");
                String stripeCustomerId = userEntity.getStripeCustomerId();
                if (StringUtils.isBlank(stripeCustomerId)) {
                    bodyClient.add("customer_email", userEmail);
                } else {
                    bodyClient.add("customer", stripeCustomerId);
                }

                JsonNode metadata = utils.convertStrToJson(plansEntity.getMetadata());
                boolean isSoftLimit = utils.jsonNodeAt(metadata, "/is_soft_limit", Boolean.class);
                if (isSoftLimit) {
                    Long upTo = plansEntity.getUpTo();
                    Double overagePrices = plansEntity.getOveragePrices();
                    String id = String.format("%s_%s_%s", apiEntity.getId(), upTo, overagePrices);
                    Optional<SoftLimitEntity> optionalSoftLimit = limitService.findByid(id);
                    String overagePriceId = "";
                    if (!optionalSoftLimit.isEmpty()) {
                        overagePriceId = optionalSoftLimit.get().getPriceId();
                    } else {
                        JsonNode result = stripeService.createPriceSoftLimit(apiEntity.getName(), String.valueOf(upTo),
                                String.valueOf(overagePrices));
                        overagePriceId = utils.jsonNodeAt(result, "/id", String.class);
                        SoftLimitEntity entity = SoftLimitEntity.builder().id(id).upTo(Long.valueOf(upTo))
                                .pricePerRequest(Double.valueOf(overagePrices)).priceId(overagePriceId).build();
                        softLimitService.save(entity);
                    }
                    bodyClient.add("line_items[1][price]", overagePriceId);
                }

                // Add metadata
                bodyClient.add("metadata[isSoftLimit]", String.valueOf(isSoftLimit));
                bodyClient.add("metadata[email]", email);
                bodyClient.add("metadata[apiId]", body.getApiId());
                bodyClient.add("metadata[planKey]", body.getPlanKey());

                String url = "https://api.stripe.com/v1/checkout/sessions";
                String responseStr = executePostRequest(String.class, url, bodyClient, headers).getBody();
                JsonNode response = utils.convertStrToJson(responseStr);
                SubscriptionChangeSubscriptionResponse subscriptionResponse = SubscriptionChangeSubscriptionResponse
                        .builder().url(utils.jsonNodeAt(response, "/url", String.class)).build();
                return ResponseEntity.ok(subscriptionResponse);
            }
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }

    @Override
    public ResponseEntity<Object> getSubscriptions(Jwt jwt) {
        try {
            String email = jwt.getClaimAsString("email");
            Optional<UserEntity> userOptional = userService.findByEmail(email);

            if (!userOptional.isPresent()) {
                return utils.badRequest("The user does not exist");
            }
            UserEntity userEntity = userOptional.get();

            List<SubscriptionEntity> subscriptionEntities = subscriptionService.findByUserId(userEntity.getId());
            if (CollectionUtils.isNotEmpty(subscriptionEntities)) {
                subscriptionEntities.forEach(subscription -> {
                    try {
                        subscriptionService.updateBillingPeriod(subscription);
                    } catch (StripeException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
            List<SubscriptionResponse> apiPlansResponses = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(subscriptionEntities)) {
                for (SubscriptionEntity subscriptionEntity : subscriptionEntities) {
                    apiPlansResponses.add(subscriptionConverter.entity2Resposne(subscriptionEntity));
                }
            }
            return ResponseEntity.ok(apiPlansResponses);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @Transactional
    @Override
    public ResponseEntity<Object> cancelSubscription(Jwt jwt, String subscriptionId) {
        try {
            Optional<SubscriptionEntity> subscriptionOptional = subscriptionService.findById(subscriptionId);
            if (subscriptionOptional.isPresent()) {
                SubscriptionEntity subscriptionEntity = subscriptionOptional.get();
                Boolean isFree = subscriptionEntity.isFree();
                if (!isFree) {
                    // cancel subscription from stripe
                    stripeService.cancelSubscription(subscriptionEntity);
                    // delete subscribe
                    subscriptionService.delete(subscriptionEntity);
                    // delete log
                    logService.deleteBySubscriptionId(subscriptionEntity.getId());
                } else {
                    subscriptionEntity.setActive(false);
                    subscriptionService.save(subscriptionEntity);
                }

                ApiPlansResponse plans = plansConverter.entity2Resposne(subscriptionEntity.getPlan());
                SubscriptionChangeSubscriptionFreeResponse response = SubscriptionChangeSubscriptionFreeResponse
                        .builder().id(subscriptionEntity.getId()).plan(plans).build();
                return ResponseEntity.ok(response);
            }
            return utils.badRequest("The subscription does not exist");
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @Override
    public ResponseEntity<Object> getQuotaUsed(String subscriptionId) {
        try {
            long quotaUsed = subscriptionService.getQuotaUsedById(subscriptionId);
            return ResponseEntity.ok(SubscriptionGetQuotaUsedResponse.builder().total(quotaUsed).build());
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}