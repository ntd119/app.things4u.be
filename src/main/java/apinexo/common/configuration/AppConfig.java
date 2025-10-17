package apinexo.common.configuration;

import java.time.Duration;
import java.util.Random;

import org.apache.catalina.connector.Connector;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate;
import org.springframework.security.config.Customizer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import apinexo.common.utils.ConstantUtils;
import okhttp3.OkHttpClient;

@Configuration
public class AppConfig {

    @Bean
    WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins(ConstantUtils.SERVER_FE)
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS").allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }

    @Bean
    RestTemplate getRestTemplate() {
        ClientHttpRequestFactory requestFactory = createRequestFactory();
        return new RestTemplate(requestFactory);
    }

    @Bean
    OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder().connectTimeout(Duration.ofSeconds(10)).readTimeout(Duration.ofSeconds(30))
                .writeTimeout(Duration.ofSeconds(15)).build();
    }

    private ClientHttpRequestFactory createRequestFactory() {
        // 40 seconds
        int timeoutInMillis = 40000;
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(timeoutInMillis);
        requestFactory.setReadTimeout(timeoutInMillis);
        return requestFactory;
    }

    @Bean
    Random getRandom() {
        return new Random();
    }

    @Bean
    ConfigurableServletWebServerFactory webServerFactory() {
        TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
        factory.addConnectorCustomizers(new TomcatConnectorCustomizer() {
            @Override
            public void customize(Connector connector) {
                connector.setProperty("relaxedQueryChars", "|{}[]^");
            }
        });
        return factory;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults()).csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(
                        auth -> auth.requestMatchers("/api/public/**").permitAll().anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }
}
