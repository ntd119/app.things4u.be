package apinexo.core.apis.playground.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "api_proxy_config")
@Getter
@Setter
public class ApiProxyConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String prefix; // VD: /zillow
    private String targetBaseUrl; // VD: http://45.63.16.213:8080/rapidapi/zillow/com
}