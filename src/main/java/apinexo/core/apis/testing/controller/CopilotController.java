package apinexo.core.apis.testing.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/copilot")
public class CopilotController {

    @GetMapping("/search")
    public ResponseEntity<Object> search(@Validated @ModelAttribute final TestingInternalRequest request) {
        return ResponseEntity.ok(Map.of("message", "Copilot API: " + request.getQuery()));
    }
}
