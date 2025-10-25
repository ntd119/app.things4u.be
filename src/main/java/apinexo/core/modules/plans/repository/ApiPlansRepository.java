package apinexo.core.modules.plans.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import apinexo.core.modules.plans.entity.ApiPlansEntity;

public interface ApiPlansRepository extends JpaRepository<ApiPlansEntity, String> {

}
