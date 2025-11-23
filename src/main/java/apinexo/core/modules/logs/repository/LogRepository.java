package apinexo.core.modules.logs.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import apinexo.core.modules.logs.entity.LogEntity;
import io.lettuce.core.dynamic.annotation.Param;

public interface LogRepository extends JpaRepository<LogEntity, String> {

    void deleteBySubscriptionId(String subscriptionId);

    List<LogEntity> findBySubscriptionId(String subscriptionId);

    @Query(value = """
            SELECT
                to_timestamp(time / 1000)::date AS date,
                COUNT(*) AS total,
                COUNT(*) FILTER (WHERE response_status != 200) AS errors
            FROM log
            WHERE subscription_id = :subscriptionId
              AND time BETWEEN :from AND :to
            GROUP BY to_timestamp(time / 1000)::date
            ORDER BY date
            """, nativeQuery = true)
    List<Object[]> countLogsGroupByDay(@Param("subscriptionId") String subscriptionId, @Param("from") Long from,
            @Param("to") Long to);

    @Query("""
                SELECT COUNT(l)
                FROM LogEntity l
                WHERE l.subscriptionId = :subscriptionId
                  AND l.time >= :startTime
            """)
    long countRequests(@Param("subscriptionId") String subscriptionId, @Param("startTime") Long startTime);
}
