package apinexo.core.modules.api.facade.impl;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import apinexo.core.modules.api.entity.ApiEntity;
import apinexo.core.modules.api.facade.ApiFacade;
import apinexo.core.modules.api.service.ApiService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ApiFacadeImpl implements ApiFacade {

    private final ApiService apiService;

    @Override
    public ResponseEntity<Object> listApis(Jwt jwt) {
        try {
            List<ApiEntity> list = apiService.findAll();
            return ResponseEntity.ok(list);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

}
