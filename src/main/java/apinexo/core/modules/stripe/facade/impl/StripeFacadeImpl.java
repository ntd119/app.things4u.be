package apinexo.core.modules.stripe.facade.impl;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import com.stripe.Stripe;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.Subscription;
import com.stripe.model.SubscriptionItem;
import com.stripe.model.SubscriptionItemCollection;
import com.stripe.model.checkout.Session;
import com.stripe.net.ApiResource;
import com.stripe.net.Webhook;

import apinexo.common.dtos.AbstractService;
import apinexo.common.utils.ApinexoUtils;
import apinexo.core.modules.api.entity.ApiEntity;
import apinexo.core.modules.api.service.ApiService;
import apinexo.core.modules.logs.service.LogService;
import apinexo.core.modules.plans.converter.PlansConverter;
import apinexo.core.modules.plans.dto.ApiPlansResponse;
import apinexo.core.modules.plans.entity.PlansEntity;
import apinexo.core.modules.stripe.facade.StripeFacade;
import apinexo.core.modules.subscription.dto.SubscriptionChangeSubscriptionFreeResponse;
import apinexo.core.modules.subscription.entity.SubscriptionEntity;
import apinexo.core.modules.subscription.service.SubscriptionService;
import apinexo.core.modules.user.entity.UserEntity;
import apinexo.core.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StripeFacadeImpl extends AbstractService implements StripeFacade {

    private final ApinexoUtils utils;

    private final UserService userService;

    private final ApiService apiService;

    private final SubscriptionService subscriptionService;

    private final LogService logService;

    private final PlansConverter plansConverter;

    @Value("${stripe.secret.endpoint}")
    private String stripeSecretEndpoint;

    @Value("${stripe.secret.key}")
    private String stripeSecret;

    @Value("${fe.server}")
    private String feServer;

    @Override
    public ResponseEntity<Object> webhook(byte[] payload, String sigHeader) {
        try {
            String payloadString = new String(payload, StandardCharsets.UTF_8);
            Event event = Webhook.constructEvent(payloadString, sigHeader, stripeSecretEndpoint);
            switch (event.getType()) {
            case "checkout.session.completed":
                EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();
                Session session = null;
                if (deserializer.getObject().isPresent()) {
                    session = (Session) deserializer.getObject().get();
                } else {
                    String rawJson = deserializer.getRawJson();
                    session = ApiResource.GSON.fromJson(rawJson, Session.class);
                }
                if (session != null) {
                    String email = session.getMetadata().get("email");

                    Optional<UserEntity> userOptional = userService.findByEmail(email);

                    if (!userOptional.isPresent()) {
                        return utils.badRequest("The user does not exist");
                    }
                    UserEntity userEntity = userOptional.get();
                    String stripeCustomerId = userEntity.getStripeCustomerId();
                    if (StringUtils.isBlank(stripeCustomerId)) {
                        stripeCustomerId = session.getCustomer();
                        userEntity.setStripeCustomerId(stripeCustomerId);
                        userService.save(userEntity);
                    }

                    String apiId = session.getMetadata().get("apiId");
                    Optional<ApiEntity> apiOptional = apiService.findbyId(apiId);
                    if (!apiOptional.isPresent()) {
                        return utils.badRequest("The api does not exist");
                    }
                    ApiEntity apiEntity = apiOptional.get();

                    List<PlansEntity> plansEntities = apiEntity.getPlans();
                    if (CollectionUtils.isEmpty(plansEntities)) {
                        return utils.badRequest("The plans does not exist");
                    }

                    String planKey = session.getMetadata().get("planKey");
                    Optional<PlansEntity> plansOptional = plansEntities.stream()
                            .filter(plan -> plan.getKey().equalsIgnoreCase(planKey)).findFirst();
                    if (!plansOptional.isPresent()) {
                        return utils.badRequest("The plans does not exist");
                    }
                    PlansEntity plansEntity = plansOptional.get();

                    String subscriptionId = session.getSubscription();
                    // delete old subscribe
                    Optional<SubscriptionEntity> subscriptionOptional = subscriptionService
                            .findByUserIdAndApiId(userEntity.getId(), apiEntity.getId());
                    if (subscriptionOptional.isPresent()) {
                        SubscriptionEntity subscriptionEntity = subscriptionOptional.get();
                        subscriptionService.delete(subscriptionEntity);
                        // delete old log
                        logService.deleteBySubscriptionId(subscriptionEntity.getId());
                    }

                    Subscription subscription = Subscription.retrieve(subscriptionId);
                    SubscriptionItemCollection items = subscription.getItems();
                    for (SubscriptionItem item : items.getData()) {
                        String subscriptionItemId = item.getId();
                    }

                    SubscriptionEntity entity = subscriptionService.save(subscriptionId, userEntity, apiEntity,
                            plansEntity);
                    ApiPlansResponse plans = plansConverter.entity2Resposne(entity.getPlan());
                    SubscriptionChangeSubscriptionFreeResponse response = SubscriptionChangeSubscriptionFreeResponse
                            .builder().id(entity.getId()).plan(plans).build();
                    return ResponseEntity.ok(response);
                }
                break;
            }
            return ResponseEntity.ok("OK");
        } catch (HttpClientErrorException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(utils.err(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(utils.err(ex.getMessage()));
        }
    }

    @Override
    public ResponseEntity<Object> createPortalSession(Jwt jwt) {
        try {
            String email = jwt.getClaimAsString("email");
            Optional<UserEntity> existing = userService.findByEmail(email);
            if (existing.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "The user does not exist"));
            }
            UserEntity userEntity = existing.get();
            Stripe.apiKey = stripeSecret;
            Map<String, Object> params = new HashMap<>();
            params.put("customer", userEntity.getStripeCustomerId());
            params.put("return_url", String.format("%s/account/billing", feServer));
            com.stripe.model.billingportal.Session session = com.stripe.model.billingportal.Session.create(params);
            return ResponseEntity.ok(Map.of("url", session.getUrl()));
        } catch (HttpClientErrorException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(utils.err(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(utils.err(ex.getMessage()));
        }
    }
}
