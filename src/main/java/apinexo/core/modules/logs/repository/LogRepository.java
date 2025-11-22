package apinexo.core.modules.logs.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import apinexo.core.modules.logs.entity.LogEntity;

public interface LogRepository extends JpaRepository<LogEntity, String> {

    void deleteBySubscriptionId(String subscriptionId);

    List<LogEntity> findBySubscriptionId(String subscriptionId);
}
