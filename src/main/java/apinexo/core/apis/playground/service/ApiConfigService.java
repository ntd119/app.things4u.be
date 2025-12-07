package apinexo.core.apis.playground.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import apinexo.core.apis.playground.entity.ApiProxyConfig;
import apinexo.core.apis.playground.repository.ApiProxyConfigRepository;

@Service
public class ApiConfigService {

    @Autowired
    private ApiProxyConfigRepository repo;

    public ApiProxyConfig findConfig(String path) {
        return repo.findAll().stream().filter(c -> path.startsWith(c.getPrefix())).findFirst().orElse(null);
    }
}