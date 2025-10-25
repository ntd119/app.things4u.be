package apinexo.core.modules.admin.facade;

import org.springframework.http.ResponseEntity;

public interface AdminFacade {

    public ResponseEntity<Object> createPlans(String apiId);
}
