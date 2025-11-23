package apinexo.core.modules.logs.facade;

import org.springframework.http.ResponseEntity;

public interface LogFacade {

    public ResponseEntity<Object> getChart(String subscriptionId, Long from, Long to);
}
