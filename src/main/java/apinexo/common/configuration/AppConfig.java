package apinexo.common.configuration;

import java.time.Duration;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.tomcat.servlet.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.OkHttpClient;

@Configuration
public class AppConfig {

    @Value("${fe.server}")
    private String feServer;

    @Value("${be.server}")
    private String beServer;

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
    WebServerFactoryCustomizer<TomcatServletWebServerFactory> customizer() {
        return factory -> factory.addConnectorCustomizers(
                (TomcatConnectorCustomizer) connector -> connector.setProperty("relaxedQueryChars", "|{}[]^"));
    }

    @Bean
    ObjectMapper objectMapper() {
        return new ObjectMapper();
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

//    @Bean
//    @Order(1)
//    SecurityFilterChain devFilterChain(HttpSecurity http) throws Exception {
//        http.securityMatcher("/dev/**")
//                .authorizeHttpRequests(
//                        auth -> auth.requestMatchers("/dev/stripe/webhook").permitAll().anyRequest().authenticated())
//                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults())).csrf(csrf -> csrf.disable())
//                .cors(Customizer.withDefaults());
//        return http.build();
//    }

//    @Bean
//    @Order(2)
//    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http.cors(cors -> cors.configurationSource(corsConfigurationSource())).csrf(csrf -> csrf.disable())
//                .authorizeHttpRequests(
//                        auth -> auth.requestMatchers("/public/**", "/portal/**").permitAll().anyRequest().permitAll());
//
//        return http.build();
//    }

//    @Bean
//    CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration config = new CorsConfiguration();
//        config.setAllowedOrigins(List.of(feServer, beServer));
//        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//        config.setAllowedHeaders(List.of("*"));
//        config.setAllowCredentials(true);
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", config);
//        return source;
//    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(feServer, beServer));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http.csrf(ServerHttpSecurity.CsrfSpec::disable).cors(Customizer.withDefaults()).authorizeExchange(
                ex -> ex.pathMatchers("/dev/**", "/dev/stripe/webhook").authenticated().anyExchange().permitAll())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults())).build();
    }

    @Bean
    WebClient webClient() {
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024) // 10MB
                ).build();

        return WebClient.builder().exchangeStrategies(strategies).build();
    }
}
