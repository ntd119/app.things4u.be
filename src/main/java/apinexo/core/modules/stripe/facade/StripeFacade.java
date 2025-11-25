package apinexo.core.modules.stripe.facade;

import org.springframework.http.ResponseEntity;

import apinexo.core.modules.stripe.dto.StripeCreatePortalSessionRequest;
import jakarta.servlet.http.HttpServletRequest;

public interface StripeFacade {

    ResponseEntity<Object> webhook(HttpServletRequest request);

    ResponseEntity<Object> createPortalSession(StripeCreatePortalSessionRequest request);
}
