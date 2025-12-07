package apinexo.core.modules.admin.facade;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;

import apinexo.core.modules.admin.dto.AdminCreateApiRequest;

public interface AdminFacade {

    public ResponseEntity<Object> createApi(Jwt jwt, AdminCreateApiRequest request);
}
