package apinexo.core.modules.subscription.service;

import java.util.Optional;

import apinexo.core.modules.subscription.entity.SubscriptionEntity;

public interface SubscriptionService {

    SubscriptionEntity save(SubscriptionEntity entity);

    void delete(SubscriptionEntity entity);

    Optional<SubscriptionEntity> findByUserIdAndApiId(String userId, String apiId);
}
