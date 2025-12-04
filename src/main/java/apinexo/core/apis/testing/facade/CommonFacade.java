package apinexo.core.apis.testing.facade;

import org.springframework.http.ResponseEntity;

public interface CommonFacade {

    ResponseEntity<?> forwardToThirdParty(String url, String method, String query, String body);
}
