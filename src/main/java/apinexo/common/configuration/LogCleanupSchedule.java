package apinexo.common.configuration;

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
        // 8 days
        long now = System.currentTimeMillis();
        long expiredTime = now - (8L * 24 * 60 * 60 * 1000);
        logFacade.deleteByTimeBefore(expiredTime);
    }
}
