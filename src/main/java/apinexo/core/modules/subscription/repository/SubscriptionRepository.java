package apinexo.core.modules.subscription.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import apinexo.core.modules.subscription.entity.SubscriptionEntity;

public interface SubscriptionRepository extends JpaRepository<SubscriptionEntity, String> {

}
