package apinexo.core.modules.apikey.facade.impl;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import apinexo.core.modules.apikey.dto.ApikeyResponse;
import apinexo.core.modules.apikey.entity.ApiKey;
import apinexo.core.modules.apikey.facade.ApiKeyFacade;
import apinexo.core.modules.apikey.service.ApiKeyService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ApiKeyFacadeImpl implements ApiKeyFacade {

    private final ApiKeyService apiKeyService;

    @Override
    public ResponseEntity<Object> getOrCreateApiKey(Jwt jwt) {
        try {
            String userId = jwt.getClaimAsString("sub");
            Optional<ApiKey> existing = apiKeyService.findByUserIdAndActiveTrue(userId);
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
        String prefix = "ak_";
        String charset = "abcdefghijklmnopqrstuvwxyz0123456789";
        int keyLength = 47;
        SecureRandom random = new SecureRandom();
        String apiKey;
        do {
            StringBuilder sb = new StringBuilder(prefix);
            for (int i = 0; i < keyLength; i++) {
                int index = random.nextInt(charset.length());
                sb.append(charset.charAt(index));
            }
            apiKey = sb.toString();
        } while (apiKeyService.existsByKeyValue(apiKey));
        ApiKey entity = ApiKey.builder().keyValue(apiKey).userId(userId).active(true).createdAt(Instant.now()).build();
        apiKeyService.save(entity);
        return apiKey;
    }

}
