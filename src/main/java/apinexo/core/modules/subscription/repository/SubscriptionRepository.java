package apinexo.core.modules.subscription.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import apinexo.core.modules.subscription.entity.SubscriptionEntity;

public interface SubscriptionRepository extends JpaRepository<SubscriptionEntity, String> {

    Optional<SubscriptionEntity> findByUser_IdAndApi_Id(String userId, String apiId);

    List<SubscriptionEntity> findByUser_Id(String userId);
}
