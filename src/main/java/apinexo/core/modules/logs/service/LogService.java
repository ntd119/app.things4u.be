package apinexo.core.modules.logs.service;

import java.util.Collection;
import java.util.List;

import apinexo.core.modules.logs.entity.LogEntity;

public interface LogService {

    LogEntity save(LogEntity entity);

    void saveAll(Collection<LogEntity> entities);

    void deleteBySubscriptionId(String subscriptionId);

    List<LogEntity> findBySubscriptionId(String subscriptionId);

    public List<Object[]> getDailyLogs(String subscriptionId, Long from, Long to);

    long countRequestByEmail(String email, Long from, Long to);

    long countRequests(String subscriptionId, Long startTime);

    void deleteByTimeBefore(Long expiredTime);
}
