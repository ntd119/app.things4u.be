package apinexo.core.modules.subscription.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.stripe.exception.StripeException;
import com.stripe.model.Subscription;

import apinexo.core.modules.admin.dto.AdminSubscriptionPageResponse;
import apinexo.core.modules.api.entity.ApiEntity;
import apinexo.core.modules.plans.entity.PlansEntity;
import apinexo.core.modules.subscription.dto.SubscriptionCached;
import apinexo.core.modules.subscription.entity.SubscriptionEntity;
import apinexo.core.modules.user.entity.UserEntity;

public interface SubscriptionService {

    SubscriptionEntity save(SubscriptionEntity entity);

    SubscriptionEntity save(String subscriptionId, UserEntity userEntity, ApiEntity apiEntity, PlansEntity plansEntity,
            String subscriptionItemId, Subscription subscription);

    void delete(SubscriptionEntity entity);

    Optional<SubscriptionEntity> findByUserIdAndApiId(String userId, String apiId);

    Optional<SubscriptionEntity> findByUserIdAndApiIdAndActiveTrue(String userId, String apiId);

    List<SubscriptionEntity> findByUserId(String userId);

    Optional<SubscriptionEntity> findById(String id);

    void increaseQuotaUsed(String subId, long value);

    void increaseQuotaUsed(String id);

    Long getQuotaUsedById(String id);

    void updateBillingPeriodFree(SubscriptionCached subscriptionEntity);

    void updateBillingPeriodFree(SubscriptionEntity subscriptionEntity);

    void updateBillingPeriod(SubscriptionEntity subscriptionEntity) throws StripeException;

    Page<AdminSubscriptionPageResponse> getSubscriptions(String keyword, Pageable pageable);

    Optional<SubscriptionCached> getSubscriptionCached(String userId, String apiId);
}
