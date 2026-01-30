package apinexo.common.redis;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RateLimitService {

    private final StringRedisTemplate redis;

    public long incrementAndGet(String subId, long windowSeconds) {
        long epochWindow = System.currentTimeMillis() / (windowSeconds * 1000);
        String key = "rate:" + subId + ":" + epochWindow;
        Long count = redis.opsForValue().increment(key);
        if (count != null && count == 1) {
            redis.expire(key, windowSeconds, TimeUnit.SECONDS);
        }
        return count == null ? 0 : count;
    }

}
