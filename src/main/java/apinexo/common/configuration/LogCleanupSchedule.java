package apinexo.common.configuration;

import java.time.Duration;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import apinexo.core.modules.logs.facade.LogFacade;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LogCleanupSchedule {

    private final LogFacade logFacade;

    // Scheduled to run every day at 2:00 AM
    @Scheduled(cron = "0 0 2 * * ?")
    public void deleteOldLogs() {
        // 7 days
        long expiredTime = System.currentTimeMillis() - Duration.ofDays(7).toMillis();
        logFacade.deleteByTimeBefore(expiredTime);
    }
}
