package apinexo.core.modules.logs.facade.impl;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import apinexo.core.modules.logs.entity.LogEntity;
import apinexo.core.modules.logs.facade.LogFacade;
import apinexo.core.modules.logs.service.LogService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LogFacadeImpl implements LogFacade {

    private final LogService logService;

    @Override
    public ResponseEntity<Object> getChart(Jwt jwt, String subscriptionId) {
        try {
            List<LogEntity> list = logService.findBySubscriptionId(subscriptionId);
            return ResponseEntity.ok(list);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
