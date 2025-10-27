package apinexo.core.modules.plans.service;

import java.util.List;
import java.util.Optional;

import apinexo.core.modules.plans.entity.ApiPlansEntity;
import apinexo.core.modules.plans.entity.PlansEntity;

public interface ApiPlansService {

    Optional<ApiPlansEntity> findByid(String id);

    ApiPlansEntity save(ApiPlansEntity entity);

    List<PlansEntity> findByApiId(String apiId);
}
