package apinexo.core.modules.logs.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import apinexo.core.modules.logs.facade.LogFacade;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dev")
public class LogController {

    private final LogFacade logFacade;

    @GetMapping("/get-chart")
    public ResponseEntity<Object> getChart(String subscriptionId, Long from, Long to) {
        return logFacade.getChart(subscriptionId, from, to);
    }

    @GetMapping("/count-request")
    public ResponseEntity<Object> countRequest(Long from, Long to) {
        return logFacade.countRequest(from, to);
    }
}
