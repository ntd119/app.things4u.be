package apinexo.core.modules.user.facade;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;

public interface UserFacade {

    public ResponseEntity<Object> getUser(Jwt jwt);
}
