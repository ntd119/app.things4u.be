package apinexo.core.modules.user.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import apinexo.core.modules.user.entity.UserEntity;
import apinexo.core.modules.user.repository.UserRepository;
import apinexo.core.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    public Optional<UserEntity> findByEmail(String userId) {
        return repository.findByEmail(userId);
    }

    @Override
    public Optional<UserEntity> findByid(String id) {
        return repository.findById(id);
    }

    @Override
    public UserEntity save(UserEntity entity) {
        return repository.save(entity);
    }
}
