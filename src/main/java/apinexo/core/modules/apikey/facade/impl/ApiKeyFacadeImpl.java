package apinexo.core.modules.apikey.facade.impl;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import apinexo.common.utils.ApinexoUtils;
import apinexo.core.modules.apikey.dto.ApikeyResponse;
import apinexo.core.modules.apikey.facade.ApiKeyFacade;
import apinexo.core.modules.user.entity.UserEntity;
import apinexo.core.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ApiKeyFacadeImpl implements ApiKeyFacade {

    private final ApinexoUtils utils;

    private final UserService userService;

    @Override
    public ResponseEntity<Object> resetApiKey(Jwt jwt) {
        try {
            String sub = jwt.getClaimAsString("sub");
            Optional<UserEntity> existing = userService.findByAuth0UserId(sub);
            if (!existing.isPresent()) {
                return ResponseEntity.badRequest().body(Map.of("message", "The user does not exist"));
            }
            String apikey = String.format("ak_%s", utils.generateRandomHexString(47));
            UserEntity entity = existing.get();
            entity.setApiKey(apikey);
            entity = userService.save(entity);
            ApikeyResponse apikeyResponse = ApikeyResponse.builder().apiKey(entity.getApiKey()).build();
            return ResponseEntity.ok(apikeyResponse);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
