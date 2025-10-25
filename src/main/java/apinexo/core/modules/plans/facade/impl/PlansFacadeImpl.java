package apinexo.core.modules.plans.facade.impl;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import apinexo.client.exception.ApiException;
import apinexo.common.dtos.AbstractService;
import apinexo.common.utils.ApinexoUtils;
import apinexo.core.modules.plans.dto.ApiPlansResponse;
import apinexo.core.modules.plans.entity.ApiPlansEntity;
import apinexo.core.modules.plans.facade.PlansFacade;
import apinexo.core.modules.plans.service.ApiPlansService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PlansFacadeImpl extends AbstractService implements PlansFacade {

    private final ApinexoUtils utils;

    private final ApiPlansService apiPlansService;

    @Override
    public ResponseEntity<Object> plans(Jwt jwt, String id) {
        try {
            Optional<ApiPlansEntity> optional = apiPlansService.findByid(id.toLowerCase());
            if (optional.isPresent()) {
                ApiPlansEntity entity = optional.get();
                ApiPlansResponse response = ApiPlansResponse.builder().id(entity.getId())
                        .basic(utils.convertStrToJson(entity.getBasic())).pro(utils.convertStrToJson(entity.getPro()))
                        .ultra(utils.convertStrToJson(entity.getUltra())).mega(utils.convertStrToJson(entity.getMega()))
                        .build();
                return ResponseEntity.ok(response);
            } else {
                return utils.badRequest("The plans does not exist");
            }
        } catch (HttpClientErrorException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(utils.err(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(utils.err(ex.getMessage()));
        }
    }
}
