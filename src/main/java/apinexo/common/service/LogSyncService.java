package apinexo.common.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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

    @Scheduled(fixedDelay = 60000 * 2) // every 5 minute
    public void scheduledSaveLog() {
        this.syncLogToDb(null);
    }

    public void syncLogToDb(String subscriptionId) {
        String lockKey = StringUtils.isNotBlank(subscriptionId) ? "lock:sync-log:" + subscriptionId
                : "lock:sync-log:all";

        Boolean locked = redis.opsForValue().setIfAbsent(lockKey, "1", 4, TimeUnit.MINUTES);

        if (locked == null || !locked)
            return;

        try {
            if (StringUtils.isNotBlank(subscriptionId)) {
                syncOneSubscription(subscriptionId);
            } else {
                syncAllSubscriptions();
            }
        } finally {
            redis.delete(lockKey);
        }
    }

    public void push(LogEntity entity, String subscriptionId) {
        try {
            String json = objectMapper.writeValueAsString(entity);
            redis.opsForSet().add("log:subs", subscriptionId);
            redis.opsForList().rightPush("log:prod:" + subscriptionId, json);
        } catch (Exception e) {
            // log.error("Failed to push log to Redis", e);
        }
    }

    private void syncAllSubscriptions() {
        var subs = redis.opsForSet().members("log:subs");
        if (CollectionUtils.isEmpty(subs))
            return;

        for (String subId : subs) {
            syncOneSubscription(subId);
        }
    }

    private void syncOneSubscription(String subscriptionId) {
        String key = "log:prod:" + subscriptionId;

        while (true) {
            List<String> batch = redis.opsForList().leftPop(key, 500);
            if (CollectionUtils.isEmpty(batch))
                break;

            List<LogEntity> logs = new ArrayList<>(batch.size());
            for (String json : batch) {
                try {
                    logs.add(objectMapper.readValue(json, LogEntity.class));
                } catch (Exception ignored) {
                }
            }

            if (!logs.isEmpty()) {
                logService.saveAll(logs);
            }
        }
    }
}
