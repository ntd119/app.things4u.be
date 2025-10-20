package apinexo.core.modules.user.facade.impl;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import apinexo.client.exception.ApiException;
import apinexo.core.modules.user.entity.UserEntity;
import apinexo.core.modules.user.facade.UserFacade;
import apinexo.core.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserFacadeImpl implements UserFacade {
    ;

    private final UserService service;

    @Override
    public ResponseEntity<Object> getUser(Jwt jwt) {
        try {
            String sub = jwt.getClaimAsString("sub");
            Optional<UserEntity> existing = service.findByAuth0UserId(sub);
            if (existing.isPresent()) {
                UserEntity entity = existing.get();
                return ResponseEntity.ok(entity);
            } else {
                return ResponseEntity.badRequest().body(new ApiException("The user does not exist"));
            }

        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
