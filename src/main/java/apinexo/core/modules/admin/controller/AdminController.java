package apinexo.core.modules.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import apinexo.core.modules.admin.dto.AdminCreateApiRequest;
import apinexo.core.modules.admin.dto.AdminCreatePriceAdditionalRequest;
import apinexo.core.modules.admin.facade.AdminFacade;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dev/admin")
public class AdminController {

    private final AdminFacade adminFacade;

    @PostMapping("/create-api")
    public ResponseEntity<Object> createApi(@RequestBody AdminCreateApiRequest request) {
        return adminFacade.createApi(request);
    }

    @PostMapping("/create-price-additional")
    public ResponseEntity<Object> createPriceAdditional(@RequestBody AdminCreatePriceAdditionalRequest request) {
        return adminFacade.createPriceAdditional(request);
    }
}
