package apinexo.core.modules.admin.facade;

import org.springframework.http.ResponseEntity;

import apinexo.core.modules.admin.dto.AdminCreateApiRequest;
import apinexo.core.modules.admin.dto.AdminCreatePriceAdditionalRequest;

public interface AdminFacade {

    public ResponseEntity<Object> createApi(AdminCreateApiRequest request);

    public ResponseEntity<Object> createPriceAdditional(AdminCreatePriceAdditionalRequest request);
}
