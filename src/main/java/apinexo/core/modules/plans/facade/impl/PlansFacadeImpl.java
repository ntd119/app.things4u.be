package apinexo.core.modules.plans.facade.impl;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import apinexo.common.dtos.AbstractService;
import apinexo.common.utils.ApinexoUtils;
import apinexo.core.modules.plans.facade.PlansFacade;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PlansFacadeImpl extends AbstractService implements PlansFacade {

    private final ApinexoUtils utils;

    @Override
    public ResponseEntity<Object> plans(Jwt jwt, String id) {
        try {
            return ResponseEntity.ok(id);
        } catch (HttpClientErrorException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(utils.err(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(utils.err(ex.getMessage()));
        }
    }
}
