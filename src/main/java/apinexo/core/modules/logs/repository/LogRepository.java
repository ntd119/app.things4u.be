package apinexo.core.modules.logs.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import apinexo.core.modules.logs.entity.LogEntity;

public interface LogRepository extends JpaRepository<LogEntity, String> {

}
