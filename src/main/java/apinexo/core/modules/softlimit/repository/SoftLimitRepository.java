package apinexo.core.modules.softlimit.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import apinexo.core.modules.softlimit.entity.SoftLimitEntity;

public interface SoftLimitRepository extends JpaRepository<SoftLimitEntity, String> {

}
