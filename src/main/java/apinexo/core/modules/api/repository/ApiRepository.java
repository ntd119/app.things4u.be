package apinexo.core.modules.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import apinexo.core.modules.api.entity.ApiEntity;

public interface ApiRepository extends JpaRepository<ApiEntity, String> {

}
