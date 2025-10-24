package apinexo.core.modules.stripe.facade;

import org.springframework.http.ResponseEntity;

public interface StripeFacade {

    ResponseEntity<Object> generateProduct();
}
