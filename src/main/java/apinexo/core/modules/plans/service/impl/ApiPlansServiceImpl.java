package apinexo.core.modules.plans.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import apinexo.core.modules.plans.entity.PlansEntity;
import apinexo.core.modules.plans.repository.PlansRepository;
import apinexo.core.modules.plans.service.ApiPlansService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApiPlansServiceImpl implements ApiPlansService {
    
    private final PlansRepository plansRepository;

    @Override
    public List<PlansEntity> findByApiId(String apiId) {
        return plansRepository.findByApi_Id(apiId);
    }
}
