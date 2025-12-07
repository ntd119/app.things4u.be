package apinexo.core.apis.playground.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import apinexo.core.apis.playground.entity.ApiProxyConfig;

public interface ApiProxyConfigRepository extends JpaRepository<ApiProxyConfig, Long> {
}