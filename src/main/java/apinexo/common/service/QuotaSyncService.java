package apinexo.common.service;

import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import apinexo.core.modules.subscription.service.SubscriptionService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuotaSyncService {

    private final StringRedisTemplate redis;
    private final SubscriptionService subscriptionService;

    @Scheduled(fixedDelay = 60000 * 5) // every 5 minute
    public void syncQuotaToDb() {

        Set<String> keys = redis.keys("quota:prod:*");

        if (keys == null || keys.isEmpty()) {
            return;
        }

        Map<String, Long> quotaMap = new HashMap<>();

        for (String key : keys) {
            String value = redis.opsForValue().get(key);
            if (value == null)
                continue;

            long count = Long.parseLong(value);

            String[] parts = key.split(":");
            String subId = parts[2];

            quotaMap.merge(subId, count, Long::sum);
        }

        // Batch update DB
        quotaMap.forEach((subId, count) -> {
            subscriptionService.increaseQuotaUsed(subId, count);
        });

        redis.delete(keys);
    }

    public void increaseQuota(String subId) {
        String key = "quota:prod:" + subId;
        Long count = redis.opsForValue().increment(key);
        // Set TTL only the first time
        if (count != null && count == 1) {
            long secondsToEndOfMonth = ChronoUnit.SECONDS.between(Instant.now(),
                    YearMonth.now(ZoneId.of("UTC")).atEndOfMonth().atTime(23, 59, 59).toInstant(ZoneOffset.UTC));
            redis.expire(key, secondsToEndOfMonth, TimeUnit.SECONDS);
        }
    }

    public long getQuotaOrZero(String subId) {
        String key = "quota:prod:" + subId;
        String value = redis.opsForValue().get(key);
        return value != null ? Long.valueOf(value) : 0L;
    }
}
