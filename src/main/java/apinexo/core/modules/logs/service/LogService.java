package apinexo.core.modules.logs.service;

import apinexo.core.modules.logs.entity.LogEntity;

public interface LogService {

    LogEntity save(LogEntity entity);

    void deleteBySubscriptionId(String subscriptionId);
}
