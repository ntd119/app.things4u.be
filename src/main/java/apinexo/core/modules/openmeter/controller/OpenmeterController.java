package apinexo.core.modules.openmeter.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import apinexo.core.modules.openmeter.facade.OpenmeterFacade;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class OpenmeterController {

    private final OpenmeterFacade openmeterFacade;

    @GetMapping("/token")
    public ResponseEntity<Object> getOrCreateApiKey() {
        return openmeterFacade.token();
    }
}
