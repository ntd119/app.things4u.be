package apinexo.core.modules.plans.facade.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import apinexo.common.dtos.AbstractService;
import apinexo.common.utils.ApinexoUtils;
import apinexo.core.modules.plans.converter.PlansConverter;
import apinexo.core.modules.plans.dto.ApiPlansResponse;
import apinexo.core.modules.plans.entity.PlansEntity;
import apinexo.core.modules.plans.facade.PlansFacade;
import apinexo.core.modules.plans.service.PlansService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PlansFacadeImpl extends AbstractService implements PlansFacade {

    private final ApinexoUtils utils;

    private final PlansService apiPlansService;

    private final PlansConverter plansConverter;

    @Override
    public ResponseEntity<Object> plans(String id) {
        try {
            List<PlansEntity> plansEntities = apiPlansService.findByApiId(id.toLowerCase());
            List<ApiPlansResponse> response = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(plansEntities)) {
                for (PlansEntity entity : plansEntities) {
                    response.add(plansConverter.entity2Resposne(entity));
                }
            }
            return ResponseEntity.ok(response);
        } catch (HttpClientErrorException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(utils.err(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(utils.err(ex.getMessage()));
        }
    }
}
