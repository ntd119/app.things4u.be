package apinexo.core.modules.plans.facade;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;

public interface PlansFacade {

    ResponseEntity<Object> plans(Jwt jwt, String id);
}
