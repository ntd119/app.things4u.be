package apinexo.core.modules.apikey.service.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import apinexo.core.modules.apikey.dto.ApikeyResponse;
import apinexo.core.modules.apikey.service.ApiKeyService;
import apinexo.core.modules.entity.ApiKey;
import apinexo.core.modules.repository.ApiKeyRepository;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApiKeyServiceImpl implements ApiKeyService {

    private final ApiKeyRepository repository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ResponseEntity<Object> getOrCreateApiKey(String authorization) {
        try {
            String token = authorization.replace("Bearer ", "").trim();

            String[] parts = token.split("\\.");
            if (parts.length < 2)
                throw new IllegalArgumentException("Invalid JWT token format");

            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));
            Map<String, Object> claims = extracted(payloadJson);

            String userId = (String) claims.get("sub");

            Optional<ApiKey> existing = repository.findByUserIdAndActiveTrue(userId);
            ApikeyResponse response = null;
            String apikey = null;
            if (existing.isPresent()) {
                apikey = existing.get().getKeyValue();
            } else {
                String issuer = (String) claims.get("iss");
                String azp = (String) claims.get("azp");
                apikey = this.generateApiKey(userId, issuer, azp);
            }
            response = ApikeyResponse.builder().apiKey(apikey).build();
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> extracted(String payloadJson) throws JsonProcessingException, JsonMappingException {
        return objectMapper.readValue(payloadJson, Map.class);
    }

    private String generateApiKey(String userId, String issuer, String azp) throws NoSuchAlgorithmException {
        String raw = userId + "|" + issuer + "|" + azp + "|" + System.currentTimeMillis();
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
        String encoded = Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        String apiKey = "ak_" + encoded.substring(0, 40).toLowerCase();
        ApiKey entity = ApiKey.builder().keyValue(apiKey).userId(userId).active(true).createdAt(Instant.now()).build();
        repository.save(entity);
        return apiKey;
    }

    public boolean validateApiKey(String apiKey) {
        return repository.findByKeyValueAndActiveTrue(apiKey).isPresent();
    }

    public boolean revokeApiKey(String apiKey) {
        Optional<ApiKey> opt = repository.findByKeyValueAndActiveTrue(apiKey);
        if (opt.isEmpty())
            return false;

        ApiKey key = opt.get();
        key.setActive(false);
        key.setRevokedAt(Instant.now());
        repository.save(key);
        return true;
    }

}
