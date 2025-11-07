package apinexo.core.modules.stripe.facade.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.checkout.Session;
import com.stripe.net.ApiResource;
import com.stripe.net.Webhook;

import apinexo.common.dtos.AbstractService;
import apinexo.common.utils.ApinexoUtils;
import apinexo.core.modules.api.entity.ApiEntity;
import apinexo.core.modules.api.service.ApiService;
import apinexo.core.modules.plans.converter.PlansConverter;
import apinexo.core.modules.plans.dto.ApiPlansResponse;
import apinexo.core.modules.plans.entity.PlansEntity;
import apinexo.core.modules.stripe.facade.StripeFacade;
import apinexo.core.modules.subscription.dto.SubscriptionChangeSubscriptionFreeResponse;
import apinexo.core.modules.subscription.entity.SubscriptionEntity;
import apinexo.core.modules.subscription.service.SubscriptionService;
import apinexo.core.modules.user.entity.UserEntity;
import apinexo.core.modules.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StripeFacadeImpl extends AbstractService implements StripeFacade {

    private final ApinexoUtils utils;

    private final UserService userService;

    private final ApiService apiService;

    private final SubscriptionService subscriptionService;

    private final PlansConverter plansConverter;

    @Value("${stripe.secret.endpoint}")
    private String stripeSecretEndpoint;

    @Override
    public ResponseEntity<Object> webhook(HttpServletRequest request) {
        try {
            String payload = new Scanner(request.getInputStream(), "UTF-8").useDelimiter("\\A").next();
            String sigHeader = request.getHeader("Stripe-Signature");
            Event event = Webhook.constructEvent(payload, sigHeader, stripeSecretEndpoint);
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
                    String sub = session.getMetadata().get("sub");

                    Optional<UserEntity> userOptional = userService.findByAuth0UserId(sub);

                    if (!userOptional.isPresent()) {
                        return utils.badRequest("The user does not exist");
                    }
                    UserEntity userEntity = userOptional.get();

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

                    String subscriptionId = utils.generateRandomHexString(24);
                    SubscriptionEntity subscribe = SubscriptionEntity.builder().id(subscriptionId).user(userEntity)
                            .api(apiEntity).plan(plansEntity).subscribedAt(LocalDateTime.now()).build();
                    subscribe.setSubscribedAt(LocalDateTime.now());
                    // delete old subscribe
                    Optional<SubscriptionEntity> subscriptionOptional = subscriptionService
                            .findByUserIdAndApiId(userEntity.getId(), apiEntity.getId());
                    if (subscriptionOptional.isPresent()) {
                        subscriptionService.delete(subscriptionOptional.get());
                    }
                    SubscriptionEntity entity = subscriptionService.save(subscribe);
                    ApiPlansResponse plans = plansConverter.entity2Resposne(entity.getPlan());
                    SubscriptionChangeSubscriptionFreeResponse response = SubscriptionChangeSubscriptionFreeResponse
                            .builder().id(entity.getId()).plan(plans).build();
                    return ResponseEntity.ok(response);
                }
                break;
            }
            return ResponseEntity.ok("ERROR");
        } catch (HttpClientErrorException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(utils.err(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(utils.err(ex.getMessage()));
        }
    }
}
