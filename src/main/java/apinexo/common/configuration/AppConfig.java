package apinexo.common.configuration;

import java.time.Duration;
import java.util.List;
import java.util.Random;

import org.apache.catalina.connector.Connector;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import apinexo.common.utils.ConstantUtils;
import okhttp3.OkHttpClient;

@Configuration
public class AppConfig {

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

//    @Bean
//    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http.cors(Customizer.withDefaults()).csrf(csrf -> csrf.disable()).authorizeHttpRequests(auth -> auth
//                .requestMatchers("/public/**", "/apis/**", "/portal/**", "/stripe/webhook", "/resend-verification")
//                .permitAll().anyRequest().authenticated())
//                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
//        return http.build();
//    }

//    @Bean
//    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//            .csrf(csrf -> csrf.disable())
//            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
//        return http.build();
//    }

    @Bean
    @Order(1)
    SecurityFilterChain devFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/dev/**")
                .authorizeHttpRequests(
                        auth -> auth.requestMatchers("/dev/stripe/webhook").permitAll().anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults())).csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    @Order(2)
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource())).csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(
                        auth -> auth.requestMatchers("/public/**", "/portal/**").permitAll().anyRequest().permitAll());

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(ConstantUtils.SERVER_FE, ConstantUtils.SERVER_BE));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
