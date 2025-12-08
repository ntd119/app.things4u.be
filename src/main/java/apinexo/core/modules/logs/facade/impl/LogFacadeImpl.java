package apinexo.core.modules.logs.facade.impl;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
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
    public ResponseEntity<Object> getChart(String subscriptionId, Long from, Long to) {
        try {
            List<Object[]> rows = logService.getDailyLogs(subscriptionId, from, to);

            Map<LocalDate, Long> totalMap = new HashMap<>();
            Map<LocalDate, Long> errorMap = new HashMap<>();

            for (Object[] row : rows) {
                LocalDate date = (LocalDate) row[0];
                Long total = ((Number) row[1]).longValue();
                Long errors = ((Number) row[2]).longValue();

                totalMap.put(date, total);
                errorMap.put(date, errors);
            }

            // convert from/to epoch ms -> LocalDate
            LocalDate fromDate = Instant.ofEpochMilli(from).atZone(ZoneId.systemDefault()).toLocalDate();

            LocalDate toDate = Instant.ofEpochMilli(to).atZone(ZoneId.systemDefault()).toLocalDate();

            List<String> days = new ArrayList<>();
            List<Long> values = new ArrayList<>();
            List<Long> errors = new ArrayList<>();

            for (LocalDate d = fromDate; !d.isAfter(toDate); d = d.plusDays(1)) {
                days.add(d.toString());
                values.add(totalMap.getOrDefault(d, 0L));
                errors.add(errorMap.getOrDefault(d, 0L));
            }
            return ResponseEntity.ok(new DayLogResponse(days, values, errors));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @Override
    public ResponseEntity<Object> countRequestByEmail(Jwt jwt, Long from, Long to) {
        try {
            String email = jwt.getClaimAsString("email");
            long count = logService.countRequestByEmail(email, from, to);
            return ResponseEntity.ok(Map.of("count", count));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
