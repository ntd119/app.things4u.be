package apinexo.core.modules.openmeter.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import apinexo.core.modules.openmeter.facade.OpenmeterFacade;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class OpenmeterController {

    private final OpenmeterFacade openmeterFacade;

    @GetMapping("/om-token")
    public ResponseEntity<Object> omToken(@AuthenticationPrincipal Jwt jwt) {
        return openmeterFacade.omToken(jwt);
    }
}
