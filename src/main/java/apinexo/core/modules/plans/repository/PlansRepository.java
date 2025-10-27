package apinexo.core.modules.plans.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import apinexo.core.modules.plans.entity.PlansEntity;

public interface PlansRepository extends JpaRepository<PlansEntity, String> {

    List<PlansEntity> findByApi_Id(String apiId);

}
