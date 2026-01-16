package apinexo.core.modules.admin.facade;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;

import apinexo.core.modules.admin.dto.AdminCreateApiRequest;
import apinexo.core.modules.admin.dto.AdminSitesUpsertRequest;

public interface AdminFacade {

    public ResponseEntity<Object> createApi(Jwt jwt, AdminCreateApiRequest request);

    public ResponseEntity<Object> sitesGetAll(Jwt jwt);

    public ResponseEntity<Object> sitesUpsert(Jwt jwt, AdminSitesUpsertRequest request);

    public ResponseEntity<Object> sitesDelete(Jwt jwt, String id);

    public ResponseEntity<Object> getSubscriptions(Jwt jwt, int page, int size);
}
