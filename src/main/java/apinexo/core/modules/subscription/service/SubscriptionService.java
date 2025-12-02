package apinexo.core.modules.subscription.service;

import java.util.List;
import java.util.Optional;

import apinexo.core.modules.api.entity.ApiEntity;
import apinexo.core.modules.plans.entity.PlansEntity;
import apinexo.core.modules.subscription.entity.SubscriptionEntity;
import apinexo.core.modules.user.entity.UserEntity;

public interface SubscriptionService {

    SubscriptionEntity save(SubscriptionEntity entity);

    SubscriptionEntity save(String subscriptionId, UserEntity userEntity, ApiEntity apiEntity, PlansEntity plansEntity,
            String subscriptionItemId);

    void delete(SubscriptionEntity entity);

    Optional<SubscriptionEntity> findByUserIdAndApiId(String userId, String apiId);

    List<SubscriptionEntity> findByUserId(String userId);

    Optional<SubscriptionEntity> findById(String id);

    void increaseQuotaUsed(String id);

    Long getQuotaUsedById(String id);

    void updateBillingPeriod(SubscriptionEntity subscriptionEntity);
}
