package apinexo.core.modules.softlimit.service;

import java.util.Optional;

import apinexo.core.modules.softlimit.entity.SoftLimitEntity;

public interface SoftLimitService {

    SoftLimitEntity save(SoftLimitEntity entity);

    Optional<SoftLimitEntity> findByid(String id);
}
