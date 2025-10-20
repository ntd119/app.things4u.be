package apinexo.core.modules.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import apinexo.core.modules.user.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, String> {

    Optional<UserEntity> findByAuth0UserId(String auth0UserId);

    Optional<UserEntity> findByApiKey(String apiKey);
}