package apinexo.core.apis.playground.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import apinexo.core.apis.playground.facade.PlaygroundFacade;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PlaygroundController {


    private final PlaygroundFacade facade;

    @RequestMapping(value = "/**", method = { RequestMethod.GET, RequestMethod.POST })
    public ResponseEntity<?> dynamicProxy(HttpServletRequest request, @RequestBody(required = false) String body) {
        return facade.dynamicProxy(request,  body);
    }
}
