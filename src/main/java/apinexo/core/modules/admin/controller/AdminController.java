package apinexo.core.modules.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import apinexo.core.modules.admin.dto.AdminCreateApiRequest;
import apinexo.core.modules.admin.dto.AdminSitesUpsertRequest;
import apinexo.core.modules.admin.facade.AdminFacade;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dev/admin")
public class AdminController {

    private final AdminFacade adminFacade;

    @PostMapping("/create-api")
    public ResponseEntity<Object> createApi(@AuthenticationPrincipal Jwt jwt,
            @RequestBody AdminCreateApiRequest request) {
        return adminFacade.createApi(jwt, request);
    }

    @GetMapping("/sites")
    public ResponseEntity<Object> sitesGetAll(@AuthenticationPrincipal Jwt jwt) {
        return adminFacade.sitesGetAll(jwt);
    }

    @PostMapping("/sites/upsert")
    public ResponseEntity<Object> sitesUpsert(@AuthenticationPrincipal Jwt jwt,
            @RequestBody AdminSitesUpsertRequest request) {
        return adminFacade.sitesUpsert(jwt, request);
    }

    @DeleteMapping("/sites/{id}")
    public ResponseEntity<Object> sitesDelete(@AuthenticationPrincipal Jwt jwt, @PathVariable String id) {
        return adminFacade.sitesDelete(jwt, id);
    }
}
