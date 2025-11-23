package apinexo.core.modules.subscription.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import apinexo.core.modules.subscription.entity.SubscriptionEntity;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.transaction.Transactional;

public interface SubscriptionRepository extends JpaRepository<SubscriptionEntity, String> {

    Optional<SubscriptionEntity> findByUser_IdAndApi_Id(String userId, String apiId);

    List<SubscriptionEntity> findByUser_Id(String userId);

    @Modifying
    @Transactional
    @Query("UPDATE SubscriptionEntity s SET s.quotaUsed = COALESCE(s.quotaUsed, 0) + 1 WHERE s.id = :id")
    void increaseQuotaUsed(@Param("id") String id);

    @Query("SELECT s.quotaUsed FROM SubscriptionEntity s WHERE s.id = :id")
    Long getQuotaUsedById(@Param("id") String id);

    @Modifying
    @Transactional
    @Query("""
                UPDATE SubscriptionEntity s
                SET s.billingPeriodFrom = :from,
                    s.billingPeriodTo   = :to
                WHERE s.id = :id
            """)
    void updateBillingPeriod(@Param("id") String id, @Param("from") Long billingPeriodFrom,
            @Param("to") Long billingPeriodTo);
}
