package apinexo.common.configuration;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import apinexo.core.modules.logs.facade.LogFacade;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LogCleanupSchedule {

    private final LogFacade logFacade;

    // Scheduled to run every Sunday at 2:00 AM
    @Scheduled(cron = "0 0 2 ? * SUN")
    public void deleteOldLogs() {
        long now = System.currentTimeMillis();
        // 100 days = 100 * 24 * 60 * 60 * 1000 ms
        long expiredTime = now - (100L * 24 * 60 * 60 * 1000);
        logFacade.deleteByTimeBefore(expiredTime);
    }
}
