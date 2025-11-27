package apinexo.core.modules.user.facade;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;

import apinexo.core.modules.user.dto.UserUpdateProfileRequest;

public interface UserFacade {

    public ResponseEntity<Object> getUser(Jwt jwt);

    public ResponseEntity<Object> resetApiKey(Jwt jwt);

    public ResponseEntity<Object> updateProfile(Jwt jwt, UserUpdateProfileRequest request);
}
