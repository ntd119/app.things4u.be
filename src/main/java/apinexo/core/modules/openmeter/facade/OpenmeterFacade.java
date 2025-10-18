package apinexo.core.modules.openmeter.facade;

import org.springframework.http.ResponseEntity;

public interface OpenmeterFacade {

    ResponseEntity<Object> token();
}
