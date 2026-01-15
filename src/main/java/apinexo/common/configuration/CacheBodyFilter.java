package apinexo.common.configuration;

import java.nio.charset.StandardCharsets;

import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.core.io.buffer.DataBuffer;
import reactor.core.publisher.Flux;

import reactor.core.publisher.Mono;

@Component
public class CacheBodyFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        String path = exchange.getRequest().getPath().value();

        if (path.startsWith("/dev/")) {
            return chain.filter(exchange);
        }

        ServerHttpRequest request = exchange.getRequest();

        if (request.getMethod() == null || request.getMethod().matches("GET|DELETE")
                || request.getHeaders().getContentLength() <= 0 || request.getHeaders().getContentType() == null
                || !request.getHeaders().getContentType().includes(MediaType.APPLICATION_JSON)) {

            return chain.filter(exchange);
        }

        return DataBufferUtils.join(request.getBody()).flatMap(buffer -> {
            byte[] bytes = new byte[buffer.readableByteCount()];
            buffer.read(bytes);
            DataBufferUtils.release(buffer);

            exchange.getAttributes().put("request_body", new String(bytes, StandardCharsets.UTF_8));

            ServerHttpRequest decoratedRequest = new ServerHttpRequestDecorator(request) {
                @Override
                public Flux<DataBuffer> getBody() {
                    return Flux.just(exchange.getResponse().bufferFactory().wrap(bytes));
                }
            };

            return chain.filter(exchange.mutate().request(decoratedRequest).build());
        });
    }
}
