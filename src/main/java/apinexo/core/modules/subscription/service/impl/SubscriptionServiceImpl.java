package apinexo.core.modules.subscription.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import apinexo.core.modules.subscription.entity.SubscriptionEntity;
import apinexo.core.modules.subscription.repository.SubscriptionRepository;
import apinexo.core.modules.subscription.service.SubscriptionService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    @Override
    public SubscriptionEntity save(SubscriptionEntity entity) {
        return subscriptionRepository.save(entity);
    }

    @Override
    public void delete(SubscriptionEntity entity) {
        subscriptionRepository.delete(entity);
    }

    @Override
    public Optional<SubscriptionEntity> findByUserIdAndApiId(String userId, String apiId) {
        return subscriptionRepository.findByUser_userIdAndApi_Id(userId, apiId);
    }
}
