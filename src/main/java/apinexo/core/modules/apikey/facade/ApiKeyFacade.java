package apinexo.core.modules.apikey.facade;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;

public interface ApiKeyFacade {

    public ResponseEntity<Object> getOrCreateApiKey(Jwt jwt);
}
