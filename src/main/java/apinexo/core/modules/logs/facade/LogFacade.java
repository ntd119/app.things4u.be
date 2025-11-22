package apinexo.core.modules.logs.facade;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;

public interface LogFacade {

    public ResponseEntity<Object> getChart(Jwt jwt, String subscriptionId, Long from, Long to);
}
