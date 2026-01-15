package apinexo.core.apis.playground.facade;

import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;

import reactor.core.publisher.Mono;

public interface PlaygroundFacade {

    Mono<ResponseEntity<String>> dynamicProxy(ServerHttpRequest request, String body);

}
