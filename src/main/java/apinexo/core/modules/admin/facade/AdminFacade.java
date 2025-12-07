package apinexo.core.modules.admin.facade;

import org.springframework.http.ResponseEntity;

import apinexo.core.modules.admin.dto.AdminCreateApiRequest;

public interface AdminFacade {

    public ResponseEntity<Object> createApi(AdminCreateApiRequest request);
}
