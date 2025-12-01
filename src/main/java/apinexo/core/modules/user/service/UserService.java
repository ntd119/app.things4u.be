package apinexo.core.modules.user.service;

import java.util.Optional;

import apinexo.core.modules.user.entity.UserEntity;

public interface UserService {

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByid(String id);

    Optional<UserEntity> findByApiKey(String apiKey);

    UserEntity save(UserEntity entity);

    String generateApiKey();

    Optional<UserEntity> findByUserName(String userName);

    Optional<UserEntity> findByUserNameAndIdNot(String userName, String id);
}
