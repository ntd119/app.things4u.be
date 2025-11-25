package apinexo.core.modules.stripe.facade;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;

import jakarta.servlet.http.HttpServletRequest;

public interface StripeFacade {

    ResponseEntity<Object> webhook(HttpServletRequest request);

    ResponseEntity<Object> createPortalSession(Jwt jwt);
}
