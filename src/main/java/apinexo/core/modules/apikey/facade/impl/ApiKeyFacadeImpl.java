package apinexo.core.modules.apikey.facade.impl;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Objects;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;

import apinexo.client.exception.ApiException;
import apinexo.common.utils.ApinexoUtils;
import apinexo.core.modules.apikey.dto.ApikeyResponse;
import apinexo.core.modules.apikey.facade.ApiKeyFacade;
import apinexo.core.modules.auth0.service.Auth0Service;
import apinexo.core.modules.openmeter.service.OpenmeterService;
import apinexo.core.modules.user.entity.UserEntity;
import apinexo.core.modules.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ApiKeyFacadeImpl implements ApiKeyFacade {

    private final ApinexoUtils utils;

    private final UserService userService;

    private final Auth0Service auth0Service;

    private final OpenmeterService openmeterService;

    @Override
    @Transactional
    public ResponseEntity<Object> getOrCreateApiKey(Jwt jwt) {
        try {
            String sub = jwt.getClaimAsString("sub");
            Optional<UserEntity> existing = userService.findByAuth0UserId(sub);
            UserEntity entity = null;
            if (existing.isPresent()) {
                entity = existing.get();
            } else {
                String apikey = this.generateApiKey();
                JsonNode user = auth0Service.getUser(sub);
                if (Objects.isNull(user) || user.isEmpty()) {
                    return ResponseEntity.badRequest().body(new ApiException("The user does not exist"));
                }

                if (utils.isArrayNode(user)) {
                    user = user.get(0);
                }

                // userId
                String userId = this.generateHexId();

                // email
                String email = utils.jsonNodeAt(user, "/email", String.class);

                // email_verified
                Boolean emailVerified = utils.jsonNodeAt(user, "/email_verified", Boolean.class);

                // first_name
                String firstName = utils.jsonNodeAt(user, "/given_name", String.class);

                // last_name
                String lastName = utils.jsonNodeAt(user, "/family_name", String.class);

                // picture
                String picture = utils.jsonNodeAt(user, "/picture", String.class);

                // auth0_user_id
                String auth0UserId = utils.jsonNodeAt(user, "/user_id", String.class);

                entity = UserEntity.builder().userId(userId).apiKey(apikey).email(email).emailVerified(emailVerified)
                        .firstName(firstName).lastName(lastName).picture(picture).auth0UserId(auth0UserId).build();
                entity = userService.save(entity);

                // openmeter: Creates or updates subject
                openmeterService.upsertSubject(userId, email);

            }
            ApikeyResponse apikeyResponse = ApikeyResponse.builder().apiKey(entity.getApiKey()).build();
            return ResponseEntity.ok(apikeyResponse);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    private String generateApiKey() throws NoSuchAlgorithmException {
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
        } while (userService.findByApiKey(apiKey).isPresent());
        return apiKey;
    }

    private String generateHexId() {
        String hex = "0123456789abcdef";
        SecureRandom random = new SecureRandom();
        int length = 24;
        String id = null;
        do {
            StringBuilder sb = new StringBuilder(length);
            for (int i = 0; i < length; i++) {
                sb.append(hex.charAt(random.nextInt(hex.length())));
            }
            id = sb.toString();
        } while (userService.findByid(id).isPresent());
        return id;
    }

}
