package apinexo.core.modules.plans.service;

import java.util.Optional;

import apinexo.core.modules.plans.entity.ApiPlansEntity;

public interface ApiPlansService {

    Optional<ApiPlansEntity> findByid(String id);

}
