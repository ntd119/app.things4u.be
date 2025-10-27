package apinexo.core.modules.plans.service;

import java.util.List;

import apinexo.core.modules.plans.entity.PlansEntity;

public interface ApiPlansService {

    List<PlansEntity> findByApiId(String apiId);
}
