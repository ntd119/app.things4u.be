package apinexo.core.modules.apikey.service;

import java.util.Optional;

import apinexo.core.modules.entity.ApiKey;

public interface ApiKeyService {

    Optional<ApiKey> findByUserIdAndActiveTrue(String userId);

    boolean existsByKeyValue(String apiKey);

    ApiKey save(ApiKey entity);
}
