package apinexo.core.modules.plans.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import apinexo.core.modules.plans.facade.PlansFacade;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/plans")
public class PlansController {

    private final PlansFacade plansFacade;

    @GetMapping("/{id}")
    public ResponseEntity<Object> plans(@AuthenticationPrincipal Jwt jwt, @PathVariable String id) {
        return plansFacade.plans(jwt, id);
    }
}
