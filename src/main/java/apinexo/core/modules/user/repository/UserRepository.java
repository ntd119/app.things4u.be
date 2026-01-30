package apinexo.core.modules.user.repository;

import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import apinexo.core.modules.user.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, String> {

    Optional<UserEntity> findByEmail(String email);

    @Cacheable(value = "apiKeyUser", key = "#apiKey")
    Optional<UserEntity> findByApiKey(String apiKey);

    Optional<UserEntity> findByUserName(String userName);

    Optional<UserEntity> findByUserNameAndIdNot(String userName, String id);
}