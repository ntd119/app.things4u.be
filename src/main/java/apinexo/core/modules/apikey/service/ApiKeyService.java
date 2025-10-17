package apinexo.core.modules.apikey.service;

import org.springframework.http.ResponseEntity;

public interface ApiKeyService {

    public  ResponseEntity<Object> getOrCreateApiKey(String authorization);
}
