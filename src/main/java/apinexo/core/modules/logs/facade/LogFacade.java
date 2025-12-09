package apinexo.core.modules.logs.facade;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;

public interface LogFacade {

    public ResponseEntity<Object> getChart(String subscriptionId, Long from, Long to);

    public ResponseEntity<Object> countRequestByEmail(Jwt jwt, Long from, Long to);

    void deleteByTimeBefore(Long expiredTime);
}
