package apinexo.core.modules.subscription.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import apinexo.core.modules.admin.dto.AdminSubscriptionPageResponse;
import apinexo.core.modules.subscription.entity.SubscriptionEntity;
import jakarta.transaction.Transactional;

public interface SubscriptionRepository extends JpaRepository<SubscriptionEntity, String> {

    Optional<SubscriptionEntity> findByUser_IdAndApi_Id(String userId, String apiId);

    Optional<SubscriptionEntity> findByUser_IdAndApi_IdAndActiveTrue(String userId, String apiId);

    List<SubscriptionEntity> findByUser_IdAndActiveTrue(String userId);

    @Modifying
    @Transactional
    @Query("UPDATE SubscriptionEntity s SET s.quotaUsed = COALESCE(s.quotaUsed, 0) + 1 WHERE s.id = :id")
    void increaseQuotaUsed(@Param("id") String id);

    @Query("SELECT s.quotaUsed FROM SubscriptionEntity s WHERE s.id = :id")
    Long getQuotaUsedById(@Param("id") String id);

    @Modifying
    @Transactional
    @Query("UPDATE SubscriptionEntity s SET s.quotaUsed = 0, s.billingPeriodFrom = :billingFrom, s.billingPeriodTo = :billingTo WHERE s.id = :id")
    void updateBillingPeriod(@Param("id") String id, @Param("billingFrom") Long billingFrom,
            @Param("billingTo") Long billingTo);

    @Query("""
                SELECT new apinexo.core.modules.admin.dto.AdminSubscriptionPageResponse(
                    s.id,
                    u.id,
                    u.userName,
                    u.email,
                    s.api.id,
                    s.plan.id,
                    s.active,
                    s.isFree,
                    s.quota,
                    s.quotaUsed,
                    s.price,
                    s.period,
                    s.subscribedAt
                )
                FROM SubscriptionEntity s
                JOIN s.user u
            """)
    Page<AdminSubscriptionPageResponse> findAllWithUser(Pageable pageable);
}
