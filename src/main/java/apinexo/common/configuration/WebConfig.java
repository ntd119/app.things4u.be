package apinexo.common.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

@Configuration
public class WebConfig implements WebFilter  {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        String path = exchange.getRequest().getPath().value();

        if (path.startsWith("/dev/")
                || path.startsWith("/portal/")
                || path.equals("/error")) {
            return chain.filter(exchange);
        }
        return chain.filter(exchange);
    }
}
