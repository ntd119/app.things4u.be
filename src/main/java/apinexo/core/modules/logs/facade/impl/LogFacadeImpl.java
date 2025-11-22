package apinexo.core.modules.logs.facade.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import apinexo.core.modules.logs.dto.DayLogResponse;
import apinexo.core.modules.logs.facade.LogFacade;
import apinexo.core.modules.logs.service.LogService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LogFacadeImpl implements LogFacade {

    private final LogService logService;

    @Override
    public ResponseEntity<Object> getChart(Jwt jwt, String subscriptionId, Long from, Long to) {
        try {
            List<Object[]> rows = logService.getDailyLogs(subscriptionId, from, to);
            Map<LocalDate, Long> totalMap = new HashMap<>();
            Map<LocalDate, Long> errorMap = new HashMap<>();

            for (Object[] row : rows) {
                LocalDate date = ((java.sql.Date) row[0]).toLocalDate();
                Long total = ((Number) row[1]).longValue();
                Long errors = ((Number) row[2]).longValue();

                totalMap.put(date, total);
                errorMap.put(date, errors);
            }

            if (totalMap.isEmpty()) {
                return ResponseEntity.ok(new DayLogResponse(List.of(), List.of(), List.of()));
            }

            // Find min - max date
            LocalDate start = Collections.min(totalMap.keySet());
            LocalDate end = Collections.max(totalMap.keySet());

            List<String> days = new ArrayList<>();
            List<Long> values = new ArrayList<>();
            List<Long> errors = new ArrayList<>();

            // Fill date range
            for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
                days.add(d.toString());
                values.add(totalMap.getOrDefault(d, 0L));
                errors.add(errorMap.getOrDefault(d, 0L));
            }
            return ResponseEntity.ok(new DayLogResponse(days, values, errors));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
