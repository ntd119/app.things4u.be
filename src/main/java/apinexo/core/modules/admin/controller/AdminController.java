package apinexo.core.modules.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import apinexo.core.modules.admin.facade.AdminFacade;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AdminFacade adminFacade;

    @GetMapping("/create-plans/{apiId}")
    public ResponseEntity<Object> createPlans(@PathVariable String apiId) {
        return adminFacade.createPlans(apiId);
    }
}
