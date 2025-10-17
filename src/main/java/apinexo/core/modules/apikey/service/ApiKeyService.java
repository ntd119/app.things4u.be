package apinexo.core.modules.apikey.service;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;

public interface ApiKeyService {

    public ResponseEntity<Object> getOrCreateApiKey(Jwt jwt);
}
