package apinexo.common.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import apinexo.core.modules.logs.entity.LogEntity;
import apinexo.core.modules.logs.service.LogService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LogSyncService {

    private final StringRedisTemplate redis;

    private final LogService logService;

    private final ObjectMapper objectMapper;

    private static final String LOG_KEY = "log:prod";

    @Scheduled(fixedDelay = 60000 * 5) // every 5 minute
    public void syncLogToDb() {

        List<String> batch = redis.opsForList().leftPop(LOG_KEY, 500);

        if (CollectionUtils.isEmpty(batch)) {
            return;
        }

        List<LogEntity> logs = new ArrayList<>();

        for (String json : batch) {
            try {
                logs.add(objectMapper.readValue(json, LogEntity.class));
            } catch (Exception e) {
                // log.error("Failed to parse log json", e);
            }
        }

        if (CollectionUtils.isNotEmpty(logs)) {
            logService.saveAll(logs);
        }
    }

    public void push(LogEntity entity) {
        try {
            String json = objectMapper.writeValueAsString(entity);
            redis.opsForList().rightPush(LOG_KEY, json);
        } catch (Exception e) {
            // log.error("Failed to push log to Redis", e);
        }
    }
}
