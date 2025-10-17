package apinexo.core.modules.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import apinexo.core.modules.entity.ApiKey;

import java.util.Optional;

public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {
    Optional<ApiKey> findByKeyValueAndActiveTrue(String keyValue);

    Optional<ApiKey> findByUserIdAndActiveTrue(String userId);

    boolean existsByKeyValue(String keyValue);
}