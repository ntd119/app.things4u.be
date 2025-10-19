package apinexo.core.modules.user.facade.impl;
import java.security.SecureRandom;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import apinexo.core.modules.user.entity.UserEntity;
import apinexo.core.modules.user.facade.UserFacade;
import apinexo.core.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserFacadeImpl implements UserFacade {

    private final UserService service;

    @Override
    public ResponseEntity<Object> getUser(Jwt jwt) {
        try {
            String email = jwt.getClaimAsString("email");
            Optional<UserEntity> existing = service.findByEmail(email);
            UserEntity entity = null;
            if (existing.isPresent()) {
                entity = existing.get();
            } else {
                // userId
                String userId = this.generateHexId();

                // email_verified
                Boolean emailVerified = jwt.getClaimAsBoolean("email_verified");

                // first_name
                String firstName = jwt.getClaimAsString("given_name");

                // last_name
                String lastName = jwt.getClaimAsString("family_name");

                // picture
                String picture = jwt.getClaimAsString("picture");

                // auth0_user_id
                String auth0UserId = jwt.getClaimAsString("sub");

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
