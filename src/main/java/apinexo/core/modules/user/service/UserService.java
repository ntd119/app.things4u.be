package apinexo.core.modules.user.service;

import java.util.Optional;

import apinexo.core.modules.user.entity.UserEntity;

public interface UserService {

    Optional<UserEntity> findByAuth0UserId(String auth0UserId);

    Optional<UserEntity> findByid(String id);

    UserEntity save(UserEntity entity);
}
