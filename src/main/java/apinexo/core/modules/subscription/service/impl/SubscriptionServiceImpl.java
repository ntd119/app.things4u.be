package apinexo.core.modules.subscription.service.impl;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Subscription;
import com.stripe.model.SubscriptionItem;

import apinexo.common.utils.ApinexoUtils;
import apinexo.common.utils.ConstantUtils;
import apinexo.core.modules.admin.dto.AdminSubscriptionPageResponse;
import apinexo.core.modules.api.entity.ApiEntity;
import apinexo.core.modules.plans.entity.PlansEntity;
import apinexo.core.modules.subscription.dto.SubscriptionCached;
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

    private final StringRedisTemplate redis;

    private final ObjectMapper objectMapper;

    @Value("${stripe.secret.key}")
    private String stripeSecret;

    @Override
    public SubscriptionEntity save(SubscriptionEntity entity) {
        return subscriptionRepository.save(entity);
    }

    @Override
    public SubscriptionEntity save(String subscriptionId, UserEntity userEntity, ApiEntity apiEntity,
            PlansEntity plansEntity, String subscriptionItemId, Subscription subscription) {
        long fromDate;
        long toDate;
        if (subscription != null && !subscription.getItems().getData().isEmpty()) {
            SubscriptionItem item = subscription.getItems().getData().get(0);
            long start = item.getCurrentPeriodStart();
            long end = item.getCurrentPeriodEnd();
            fromDate = start > 0 ? start * 1000 : 0;
            toDate = end > 0 ? end * 1000 : 0;
        } else {
            fromDate = 0;
            toDate = 0;
        }
        if (fromDate == 0 || toDate == 0) {
            LocalDateTime now = utils.getCurrentDateTime(ConstantUtils.TIME_ZONE_UCT);
            fromDate = now.atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();
            toDate = now.plusMonths(1).atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();
        }

        JsonNode metadata = utils.convertStrToJson(plansEntity.getMetadata());
        SubscriptionEntity subscribe = SubscriptionEntity.builder().id(subscriptionId).user(userEntity).api(apiEntity)
                .plan(plansEntity).subscribedAt(LocalDateTime.now()).billingPeriodFrom(fromDate).billingPeriodTo(toDate)
                .currentPlan(plansEntity.getNickname()).quota(plansEntity.getUpTo()).period(plansEntity.getPeriod())
                .rateLimit(utils.jsonNodeAt(metadata, "/rate_limit", Long.class))
                .rateLimitPeriod(utils.jsonNodeAt(metadata, "/rate_limit_period", String.class))
                .isSoftLimit(utils.jsonNodeAt(metadata, "/is_soft_limit", Boolean.class))
                .isRateLimit(utils.jsonNodeAt(metadata, "/is_rate_limit", Boolean.class))
                .overagePrices(plansEntity.getOveragePrices()).price(plansEntity.getPrice())
                .isFree(plansEntity.getIsFree()).subscriptionItemId(subscriptionItemId).build();
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
    public Optional<SubscriptionEntity> findByUserIdAndApiIdAndActiveTrue(String userId, String apiId) {
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(apiId)) {
            return Optional.empty();
        }
        return subscriptionRepository.findByUser_IdAndApi_IdAndActiveTrue(userId, apiId.toLowerCase());
    }

    @Override
    public List<SubscriptionEntity> findByUserId(String userId) {
        return subscriptionRepository.findByUser_IdAndActiveTrue(userId);
    }

    @Override
    public Optional<SubscriptionEntity> findById(String id) {
        return subscriptionRepository.findById(id);
    }

    @Override
    public void increaseQuotaUsed(String subId, long value) {
        subscriptionRepository.increaseQuotaUsed(subId, value);
    }

    @Override
    public void increaseQuotaUsed(String id) {
        subscriptionRepository.increaseQuotaUsed(id);
    }

    @Override
    public Long getQuotaUsedById(String id) {
        return subscriptionRepository.getQuotaUsedById(id);
    }

    @Override
    public void updateBillingPeriodFree(SubscriptionCached subscriptionEntity) {
        long currentDate = utils.milliseconds();
        long toDate = subscriptionEntity.getBillingPeriodTo();
        if (currentDate > toDate) {
            Boolean isFree = subscriptionEntity.isFree();
            long fromDate;
            if (Objects.isNull(isFree) || isFree.booleanValue()) {
                fromDate = subscriptionEntity.getBillingPeriodFrom();
                while (currentDate > toDate) {
                    fromDate = toDate;
                    // +1 month
                    toDate = addOneMonth(fromDate);
                }
                subscriptionEntity.setBillingPeriodFrom(fromDate);
                subscriptionEntity.setBillingPeriodTo(toDate);
                subscriptionEntity.setQuotaUsed(0);
                subscriptionRepository.updateBillingPeriod(subscriptionEntity.getId(), fromDate, toDate);
            }
        }
    }

    @Override
    public void updateBillingPeriodFree(SubscriptionEntity subscriptionEntity) {
        long currentDate = utils.milliseconds();
        long toDate = subscriptionEntity.getBillingPeriodTo();
        if (currentDate > toDate) {
            Boolean isFree = subscriptionEntity.isFree();
            long fromDate;
            if (Objects.isNull(isFree) || isFree.booleanValue()) {
                fromDate = subscriptionEntity.getBillingPeriodFrom();
                while (currentDate > toDate) {
                    fromDate = toDate;
                    // +1 month
                    toDate = addOneMonth(fromDate);
                }
                subscriptionEntity.setBillingPeriodFrom(fromDate);
                subscriptionEntity.setBillingPeriodTo(toDate);
                subscriptionEntity.setQuotaUsed(0);
                subscriptionRepository.updateBillingPeriod(subscriptionEntity.getId(), fromDate, toDate);
            }
        }
    }

    @Override
    public void updateBillingPeriod(SubscriptionEntity subscriptionEntity) throws StripeException {
        long toDate = subscriptionEntity.getBillingPeriodTo();
        long fromDate;
        Stripe.apiKey = stripeSecret;
        Subscription subscription = Subscription.retrieve(subscriptionEntity.getId());
        if (subscription != null && !subscription.getItems().getData().isEmpty()) {
            SubscriptionItem item = subscription.getItems().getData().get(0);
            long start = item.getCurrentPeriodStart();
            long end = item.getCurrentPeriodEnd();
            fromDate = start > 0 ? start * 1000 : 0;
            toDate = end > 0 ? end * 1000 : 0;
        } else {
            fromDate = 0;
            toDate = 0;
        }
        if (fromDate == 0 || toDate == 0) {
            LocalDateTime now = utils.getCurrentDateTime(ConstantUtils.TIME_ZONE_UCT);
            fromDate = now.atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();
            toDate = now.plusMonths(1).atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();
        }
        subscriptionEntity.setBillingPeriodFrom(fromDate);
        subscriptionEntity.setBillingPeriodTo(toDate);
        subscriptionEntity.setQuotaUsed(0);
        subscriptionRepository.updateBillingPeriod(subscriptionEntity.getId(), fromDate, toDate);
    }

    private long addOneMonth(long timestampMillis) {
        return Instant.ofEpochMilli(timestampMillis).atZone(ZoneId.of("UTC")).plusMonths(1).toInstant().toEpochMilli();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AdminSubscriptionPageResponse> getSubscriptions(String keyword, Pageable pageable) {
        return subscriptionRepository.findAllWithUser(keyword, pageable);
    }

    @Override
    public Optional<SubscriptionCached> getSubscriptionCached(String userId, String apiId) {
        String key = ConstantUtils.REDIS_KEY_SUB + ":" + userId + ":" + apiId;
        String cached = redis.opsForValue().get(key);
        if (cached != null) {
            try {
                return Optional.of(objectMapper.readValue(cached, SubscriptionCached.class));
            } catch (Exception e) {
                redis.delete(key); // corrupted cache
            }
        }

        Optional<SubscriptionEntity> opt = findByUserIdAndApiIdAndActiveTrue(userId, apiId);

        opt.ifPresent(sub -> {
            SubscriptionCached dto = SubscriptionCached.builder().id(sub.getId())
                    .billingPeriodFrom(sub.getBillingPeriodFrom()).billingPeriodTo(sub.getBillingPeriodTo())
                    .active(sub.isActive()).isFree(sub.isFree()).currentPlan(sub.getCurrentPlan()).quota(sub.getQuota())
                    .period(sub.getPeriod()).quotaUsed(sub.getQuotaUsed()).rateLimit(sub.getRateLimit())
                    .rateLimitPeriod(sub.getRateLimitPeriod()).isSoftLimit(sub.getIsSoftLimit())
                    .overagePrices(sub.getOveragePrices()).isRateLimit(sub.getIsRateLimit()).price(sub.getPrice())
                    .subscriptionItemId(sub.getSubscriptionItemId()).apiId(sub.getApi().getId()).build();
            try {
                redis.opsForValue().set(key, objectMapper.writeValueAsString(dto), Duration.ofMinutes(5) // TTL
                );
            } catch (Exception ignored) {
                System.out.println("Erro");
            }
        });

        return opt.map(sub -> SubscriptionCached.builder().id(sub.getId()).billingPeriodFrom(sub.getBillingPeriodFrom())
                .billingPeriodTo(sub.getBillingPeriodTo()).active(sub.isActive()).isFree(sub.isFree())
                .currentPlan(sub.getCurrentPlan()).quota(sub.getQuota()).period(sub.getPeriod())
                .quotaUsed(sub.getQuotaUsed()).rateLimit(sub.getRateLimit()).rateLimitPeriod(sub.getRateLimitPeriod())
                .isSoftLimit(sub.getIsSoftLimit()).overagePrices(sub.getOveragePrices())
                .isRateLimit(sub.getIsRateLimit()).price(sub.getPrice()).subscriptionItemId(sub.getSubscriptionItemId())
                .apiId(sub.getApi().getId()).build());
    }
}
