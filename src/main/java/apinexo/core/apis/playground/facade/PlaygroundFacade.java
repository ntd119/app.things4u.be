package apinexo.core.apis.playground.facade;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

public interface PlaygroundFacade {

    ResponseEntity<?> forwardToThirdParty(HttpHeaders headers, String url, String method, String query, String body);
}
