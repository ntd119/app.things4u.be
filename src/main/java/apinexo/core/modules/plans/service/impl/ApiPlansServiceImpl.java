package apinexo.core.modules.plans.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import apinexo.core.modules.plans.entity.ApiPlansEntity;
import apinexo.core.modules.plans.repository.ApiPlansRepository;
import apinexo.core.modules.plans.service.ApiPlansService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApiPlansServiceImpl implements ApiPlansService {

    private final ApiPlansRepository apiPlansRepository;

    @Override
    public Optional<ApiPlansEntity> findByid(String id) {
        return apiPlansRepository.findById(id);
    }
}
