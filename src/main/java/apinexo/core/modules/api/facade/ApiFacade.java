package apinexo.core.modules.api.facade;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;

public interface ApiFacade {

    public ResponseEntity<Object> listApis(Jwt jwt);
}
