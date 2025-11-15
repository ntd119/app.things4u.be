package apinexo.core.apis.jsearch.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/apis/jsearch")
public class JsearchController {

    @GetMapping("/search")
    public ResponseEntity<Object> search() {
        return ResponseEntity.ok(Map.of("message", "OK"));
    }
}
