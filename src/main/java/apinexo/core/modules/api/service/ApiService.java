package apinexo.core.modules.api.service;

import java.util.Optional;

import apinexo.core.modules.api.entity.ApiEntity;

public interface ApiService {

    ApiEntity save(ApiEntity entity);

    Optional<ApiEntity> findbyId(String id);
}
