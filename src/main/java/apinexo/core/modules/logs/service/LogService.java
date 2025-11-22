package apinexo.core.modules.logs.service;

import java.util.List;

import apinexo.core.modules.logs.entity.LogEntity;

public interface LogService {

    LogEntity save(LogEntity entity);

    void deleteBySubscriptionId(String subscriptionId);

    List<LogEntity> findBySubscriptionId(String subscriptionId);

    public List<Object[]> getDailyLogs(String subscriptionId, Long from, Long to);
}
