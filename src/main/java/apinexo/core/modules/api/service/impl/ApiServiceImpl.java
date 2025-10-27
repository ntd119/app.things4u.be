package apinexo.core.modules.api.service.impl;

import org.springframework.stereotype.Service;

import apinexo.core.modules.api.entity.ApiEntity;
import apinexo.core.modules.api.repository.ApiRepository;
import apinexo.core.modules.api.service.ApiService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApiServiceImpl  implements ApiService {
    
    private final ApiRepository apiRepository;

    @Override
    public ApiEntity save(ApiEntity entity) {
        return apiRepository.save(entity);
    }
}
