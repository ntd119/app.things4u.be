package apinexo.core.modules.subscription.service.impl;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
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
