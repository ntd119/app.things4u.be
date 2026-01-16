package apinexo.core.modules.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import apinexo.core.modules.api.entity.ApiEntity;

public interface ApiRepository extends JpaRepository<ApiEntity, String> {

    @Query("""
                SELECT a FROM ApiEntity a
                LEFT JOIN FETCH a.plans
                WHERE a.id = :id
            """)
    Optional<ApiEntity> findByIdWithPlans(@Param("id") String id);
}
