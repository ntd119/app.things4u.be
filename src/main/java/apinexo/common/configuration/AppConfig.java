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
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import okhttp3.OkHttpClient;

@Configuration
@EnableScheduling
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

    @Bean
    ProtobufHttpMessageConverter protobufHttpMessageConverter() {
        return new ProtobufHttpMessageConverter();
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
}
