package apinexo.core.modules.apikey.service.impl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import apinexo.core.modules.apikey.dto.ApikeyResponse;
import apinexo.core.modules.apikey.service.ApiKeyService;
import apinexo.core.modules.entity.ApiKey;
import apinexo.core.modules.repository.ApiKeyRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApiKeyServiceImpl implements ApiKeyService {

    private final ApiKeyRepository repository;

    @Override
    public ResponseEntity<Object> getOrCreateApiKey(Jwt jwt) {
        try {
            String userId = jwt.getClaimAsString("sub");
            Optional<ApiKey> existing = repository.findByUserIdAndActiveTrue(userId);
            String apikey = null;
            if (existing.isPresent()) {
                apikey = existing.get().getKeyValue();
            } else {
                String issuer = jwt.getIssuer() != null ? jwt.getIssuer().toString() : null;
                String azp = jwt.getClaimAsString("azp");
                apikey = this.generateApiKey(userId, issuer, azp);
            }
            return ResponseEntity.ok(ApikeyResponse.builder().apiKey(apikey).build());
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
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
