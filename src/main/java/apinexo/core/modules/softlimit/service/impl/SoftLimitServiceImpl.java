package apinexo.core.modules.softlimit.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import apinexo.core.modules.softlimit.entity.SoftLimitEntity;
import apinexo.core.modules.softlimit.repository.SoftLimitRepository;
import apinexo.core.modules.softlimit.service.SoftLimitService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SoftLimitServiceImpl implements SoftLimitService {

    private final SoftLimitRepository softLimitRepository;

    @Override
    public SoftLimitEntity save(SoftLimitEntity entity) {
        return softLimitRepository.save(entity);
    }

    @Override
    public Optional<SoftLimitEntity> findByid(String id) {
        return softLimitRepository.findById(id);
    }
}
