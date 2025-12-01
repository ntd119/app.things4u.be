package apinexo.core.modules.user.facade.impl;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;

import apinexo.client.exception.ApiException;
import apinexo.common.utils.ApinexoUtils;
import apinexo.core.modules.auth0.service.Auth0Service;
import apinexo.core.modules.user.dto.UserGetUserResponse;
import apinexo.core.modules.user.dto.UserUpdateProfileRequest;
import apinexo.core.modules.user.entity.UserEntity;
import apinexo.core.modules.user.facade.UserFacade;
import apinexo.core.modules.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserFacadeImpl implements UserFacade {

    private final ApinexoUtils utils;

    private final UserService userService;

    private final Auth0Service auth0Service;

    @Transactional
    @Override
    public ResponseEntity<Object> getUser(Jwt jwt) {
        try {
            String emailReq = jwt.getClaimAsString("email");
            Optional<UserEntity> existing = userService.findByEmail(emailReq);
            UserEntity entity = null;
            if (existing.isPresent()) {
                entity = existing.get();
            } else {
                String sub = jwt.getClaimAsString("sub");
                JsonNode user = auth0Service.getUser(sub).getBody();
                if (Objects.isNull(user) || user.isEmpty()) {
                    return ResponseEntity.badRequest().body(new ApiException("The user does not exist"));
                }

                if (utils.isArrayNode(user)) {
                    user = user.get(0);
                }

                // userId
                String userId = utils.generateRandomHexString(24);

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

                entity = UserEntity.builder().id(userId).apiKey(userService.generateApiKey()).email(email)
                        .emailVerified(emailVerified).firstName(firstName).lastName(lastName).picture(picture)
                        .auth0UserId(auth0UserId).build();
                entity = userService.save(entity);

            }
            UserGetUserResponse response = UserGetUserResponse.builder().userId(entity.getId()).email(entity.getEmail())
                    .emailVerified(entity.getEmailVerified()).firstName(entity.getFirstName())
                    .lastName(entity.getLastName()).userName(entity.getUserName()).picture(entity.getPicture())
                    .auth0UserId(entity.getAuth0UserId()).stripeCustomerId(entity.getStripeCustomerId())
                    .api_key(entity.getApiKey()).build();
            return ResponseEntity.ok(response);

        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @Override
    public ResponseEntity<Object> resetApiKey(Jwt jwt) {
        try {
            String email = jwt.getClaimAsString("email");
            Optional<UserEntity> existing = userService.findByEmail(email);
            if (!existing.isPresent()) {
                return ResponseEntity.badRequest().body(Map.of("message", "The user does not exist"));
            }
            UserEntity entity = existing.get();
            entity.setApiKey(userService.generateApiKey());
            entity = userService.save(entity);
            UserGetUserResponse response = UserGetUserResponse.builder().userId(entity.getId()).email(entity.getEmail())
                    .emailVerified(entity.getEmailVerified()).firstName(entity.getFirstName())
                    .lastName(entity.getLastName()).userName(entity.getUserName()).picture(entity.getPicture())
                    .auth0UserId(entity.getAuth0UserId()).stripeCustomerId(entity.getStripeCustomerId())
                    .api_key(entity.getApiKey()).build();
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @Override
    public ResponseEntity<Object> updateProfile(Jwt jwt, UserUpdateProfileRequest request) {
        try {
            String email = jwt.getClaimAsString("email");
            Optional<UserEntity> existing = userService.findByEmail(email);
            if (!existing.isPresent()) {
                return ResponseEntity.badRequest().body(Map.of("message", "The user does not exist"));
            }

            // check user name
            String userName = request.getUserName();
            if (StringUtils.isBlank(userName)) {
                return ResponseEntity.badRequest().body(Map.of("message", "Username must not be empty"));
            }

            UserEntity entity = existing.get();
            userName = userName.trim();
            Optional<UserEntity> optional = userService.findByUserNameAndIdNot(userName, entity.getId());
            if (optional.isPresent()) {
                return ResponseEntity.badRequest().body(Map.of("message", "The username already exists"));
            }

            entity.setFirstName(request.getFirstName());
            entity.setLastName(request.getLastName());
            entity.setUserName(userName);
            entity = userService.save(entity);
            UserGetUserResponse response = UserGetUserResponse.builder().userId(entity.getId()).email(entity.getEmail())
                    .emailVerified(entity.getEmailVerified()).firstName(entity.getFirstName())
                    .lastName(entity.getLastName()).userName(entity.getUserName()).picture(entity.getPicture())
                    .auth0UserId(entity.getAuth0UserId()).stripeCustomerId(entity.getStripeCustomerId())
                    .api_key(entity.getApiKey()).build();
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
