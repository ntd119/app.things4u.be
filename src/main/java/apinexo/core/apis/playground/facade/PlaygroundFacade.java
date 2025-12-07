package apinexo.core.apis.playground.facade;

import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpServletRequest;

public interface PlaygroundFacade {

    ResponseEntity<?> dynamicProxy(HttpServletRequest request, String body);

}
