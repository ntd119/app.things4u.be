package apinexo.core.modules.apikey.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import apinexo.core.modules.apikey.service.ApiKeyService;
import apinexo.core.modules.entity.ApiKey;
import apinexo.core.modules.repository.ApiKeyRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApiKeyServiceImpl implements ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;

    @Override
    public Optional<ApiKey> findByUserIdAndActiveTrue(String userId) {
        return apiKeyRepository.findByUserIdAndActiveTrue(userId);
    }

    @Override
    public boolean existsByKeyValue(String apiKey) {
        return apiKeyRepository.existsByKeyValue(apiKey);
    }
    
    @Override
    public ApiKey save(ApiKey entity) {
        return apiKeyRepository.save(entity);
    }
}
