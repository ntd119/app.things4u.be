package apinexo.core.modules.stripe.facade;

import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpServletRequest;

public interface StripeFacade {

    ResponseEntity<Object> webhook(HttpServletRequest request);
}
