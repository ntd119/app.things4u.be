package apinexo.core.modules.subscription.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import apinexo.common.utils.ApinexoUtils;
import apinexo.common.utils.ConstantUtils;
import apinexo.core.modules.api.entity.ApiEntity;
import apinexo.core.modules.plans.entity.PlansEntity;
import apinexo.core.modules.subscription.entity.SubscriptionEntity;
import apinexo.core.modules.subscription.repository.SubscriptionRepository;
import apinexo.core.modules.subscription.service.SubscriptionService;
import apinexo.core.modules.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final ApinexoUtils utils;

    private final SubscriptionRepository subscriptionRepository;

    @Override
    public SubscriptionEntity save(SubscriptionEntity entity) {
        return subscriptionRepository.save(entity);
    }

    @Override
    public SubscriptionEntity save(String subscriptionId, UserEntity userEntity, ApiEntity apiEntity,
            PlansEntity plansEntity) {
        LocalDateTime fromDate = utils.getCurrentDateTime(ConstantUtils.TIME_ZONE_UCT);
        LocalDateTime toDate = fromDate.plusMonths(1);
        JsonNode metadata = utils.convertStrToJson(plansEntity.getMetadata());
        SubscriptionEntity subscribe = SubscriptionEntity.builder().id(subscriptionId).user(userEntity).api(apiEntity)
                .plan(plansEntity).subscribedAt(LocalDateTime.now())
                .billingPeriodFrom(fromDate.atZone(ZoneId.of("UTC")).toInstant().toEpochMilli())
                .billingPeriodTo(toDate.atZone(ZoneId.of("UTC")).toInstant().toEpochMilli())
                .currentPlan(plansEntity.getNickname()).quota(plansEntity.getUpTo()).period(plansEntity.getPeriod())
                .rateLimit(utils.jsonNodeAt(metadata, "/rate_limit", Long.class))
                .rateLimitPeriod(utils.jsonNodeAt(metadata, "/rate_limit_period", String.class))
                .isSoftLimit(utils.jsonNodeAt(metadata, "/is_soft_limit", Boolean.class))
                .overagePrices(plansEntity.getOveragePrices()).build();
        subscribe.setSubscribedAt(LocalDateTime.now());

        return subscriptionRepository.save(subscribe);
    }

    @Override
    public void delete(SubscriptionEntity entity) {
        subscriptionRepository.delete(entity);
    }

    @Override
    public Optional<SubscriptionEntity> findByUserIdAndApiId(String userId, String apiId) {
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(apiId)) {
            return Optional.empty();
        }
        return subscriptionRepository.findByUser_IdAndApi_Id(userId, apiId.toLowerCase());
    }

    @Override
    public List<SubscriptionEntity> findByUserId(String userId) {
        return subscriptionRepository.findByUser_Id(userId);
    }

    @Override
    public Optional<SubscriptionEntity> findById(String id) {
        return subscriptionRepository.findById(id);
    }
}
