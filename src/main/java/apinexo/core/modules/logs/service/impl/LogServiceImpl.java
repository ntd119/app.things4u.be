package apinexo.core.modules.logs.service.impl;

import org.springframework.stereotype.Service;

import apinexo.core.modules.logs.entity.LogEntity;
import apinexo.core.modules.logs.repository.LogRepository;
import apinexo.core.modules.logs.service.LogService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LogServiceImpl implements LogService {

    private final LogRepository logRepository;

    @Override
    public LogEntity save(LogEntity entity) {
        return logRepository.save(entity);
    }

    @Override
    public void deleteBySubscriptionId(String subscriptionId) {
        logRepository.deleteBySubscriptionId(subscriptionId);
    }
}
