package apinexo.core.modules.plans.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import apinexo.core.modules.plans.entity.PlansEntity;
import apinexo.core.modules.plans.repository.PlansRepository;
import apinexo.core.modules.plans.service.PlansService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlansServiceImpl implements PlansService {
    
    private final PlansRepository plansRepository;

    @Override
    public List<PlansEntity> findByApiId(String apiId) {
        return plansRepository.findByApi_Id(apiId);
    }
}
