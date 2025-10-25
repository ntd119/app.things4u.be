package apinexo.core.modules.plans.facade;

import org.springframework.http.ResponseEntity;

public interface PlansFacade {

    ResponseEntity<Object> plans(String id);
}
