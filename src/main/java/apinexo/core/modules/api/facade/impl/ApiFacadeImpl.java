package apinexo.core.modules.api.facade.impl;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import apinexo.core.modules.api.facade.ApiFacade;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ApiFacadeImpl implements ApiFacade {

    @Override
    @Transactional
    public ResponseEntity<Object> listApis(Jwt jwt) {
        try {
            
            return ResponseEntity.ok("OK");
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

}
