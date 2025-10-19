package apinexo.core.modules.user.service;

import java.util.Optional;

import apinexo.core.modules.user.entity.UserEntity;

public interface UserService {

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByid(String id);

    UserEntity save(UserEntity entity);
}
