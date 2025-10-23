package apinexo.core.modules.openmeter.facade;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;

public interface OpenmeterFacade {

    ResponseEntity<Object> omToken(Jwt jwt);

    ResponseEntity<Object> sendEvent(Jwt jwt);

    ResponseEntity<Object> generate();
}
