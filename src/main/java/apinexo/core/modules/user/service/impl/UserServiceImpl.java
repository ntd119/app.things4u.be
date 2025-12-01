package apinexo.core.modules.user.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import apinexo.common.utils.ApinexoUtils;
import apinexo.core.modules.user.entity.UserEntity;
import apinexo.core.modules.user.repository.UserRepository;
import apinexo.core.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final ApinexoUtils utils;

    private final UserRepository repository;

    @Override
    public Optional<UserEntity> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    @Override
    public Optional<UserEntity> findByid(String id) {
        return repository.findById(id);
    }

    @Override
    public UserEntity save(UserEntity entity) {
        return repository.save(entity);
    }

    @Override
    public Optional<UserEntity> findByApiKey(String apiKey) {
        return repository.findByApiKey(apiKey);
    }

    @Override
    public String generateApiKey() {
        for (int i = 0; i < 10; i++) {
            String apiKey = "ak_" + utils.generateRandomHexString(47);
            if (findByApiKey(apiKey).isEmpty()) {
                return apiKey;
            }
        }
        throw new IllegalStateException("Unable to generate unique API key after multiple attempts");
    }

    @Override
    public Optional<UserEntity> findByUserName(String userName) {
        return repository.findByUserName(userName);
    }

    @Override
    public Optional<UserEntity> findByUserNameAndIdNot(String userName, String id) {
        return repository.findByUserNameAndIdNot(userName, id);
    }
}
