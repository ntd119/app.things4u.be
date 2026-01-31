package apinexo.common.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

    @Scheduled(fixedDelay = 60000 * 5) // every 5 minute
    public void syncLogToDb() {

        Boolean locked = redis.opsForValue().setIfAbsent("lock:sync-log", "1", 4, TimeUnit.MINUTES);

        if (locked == null || !locked) {
            return;
        }

        try {
            List<String> batch = redis.opsForList().leftPop("log:prod", 500);

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
        } finally {
            redis.delete("lock:sync-log");
        }
    }

    public void push(LogEntity entity) {
        try {
            String json = objectMapper.writeValueAsString(entity);
            redis.opsForList().rightPush("log:prod", json);
        } catch (Exception e) {
            // log.error("Failed to push log to Redis", e);
        }
    }
}
