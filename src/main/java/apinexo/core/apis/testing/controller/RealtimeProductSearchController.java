package apinexo.core.apis.testing.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/realtime-product-search")
public class RealtimeProductSearchController {

    @GetMapping("/search")
    public ResponseEntity<Object> search() {
        return ResponseEntity.ok(Map.of("message", "Real-Time Product Search"));
    }
}
