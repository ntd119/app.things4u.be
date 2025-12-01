package apinexo.core.modules.stripe.facade;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;

public interface StripeFacade {

    ResponseEntity<Object> webhook( byte[] payload, String sigHeader);

    ResponseEntity<Object> createPortalSession(Jwt jwt);
}
