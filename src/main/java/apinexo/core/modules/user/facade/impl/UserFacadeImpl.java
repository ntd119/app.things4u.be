package apinexo.core.modules.user.facade.impl;

import java.security.SecureRandom;
import java.util.Objects;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;

import apinexo.client.exception.ApiException;
import apinexo.common.utils.ApinexoUtils;
import apinexo.core.modules.auth0.facade.Auth0Facade;
import apinexo.core.modules.user.entity.UserEntity;
import apinexo.core.modules.user.facade.UserFacade;
import apinexo.core.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserFacadeImpl implements UserFacade {

    private final ApinexoUtils utils;

    private final UserService service;

    private final Auth0Facade auth0Facade;

    @Override
    public ResponseEntity<Object> getUser(Jwt jwt) {
        try {
            String sub = jwt.getClaimAsString("sub");
            Optional<UserEntity> existing = service.findBySub(sub);
            UserEntity entity = null;
            if (existing.isPresent()) {
                entity = existing.get();
            } else {
                JsonNode user = auth0Facade.getUser(sub);
                if (Objects.isNull(user) || user.isEmpty()) {
                    return ResponseEntity.badRequest().body(new ApiException("The user does not exist"));
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

                entity = UserEntity.builder().userId(userId).email(email).emailVerified(emailVerified)
                        .firstName(firstName).lastName(lastName).picture(picture).auth0UserId(auth0UserId).build();
                entity = service.save(entity);
            }
            return ResponseEntity.ok(entity);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
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
        } while (service.findByid(id).isPresent());
        return id;
    }
}
