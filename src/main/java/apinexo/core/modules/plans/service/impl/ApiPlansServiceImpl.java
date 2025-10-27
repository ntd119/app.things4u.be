package apinexo.core.modules.plans.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import apinexo.core.modules.plans.entity.ApiPlansEntity;
import apinexo.core.modules.plans.entity.PlansEntity;
import apinexo.core.modules.plans.repository.ApiPlansRepository;
import apinexo.core.modules.plans.repository.PlansRepository;
import apinexo.core.modules.plans.service.ApiPlansService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApiPlansServiceImpl implements ApiPlansService {

    private final ApiPlansRepository apiPlansRepository;
    
    private final PlansRepository plansRepository;

    @Override
    public Optional<ApiPlansEntity> findByid(String id) {
        return apiPlansRepository.findById(id);
    }

    @Override
    public ApiPlansEntity save(ApiPlansEntity entity) {
        return apiPlansRepository.save(entity);
    }

    @Override
    public List<PlansEntity> findByApiId(String apiId) {
        return plansRepository.findByApi_Id(apiId);
    }
}
