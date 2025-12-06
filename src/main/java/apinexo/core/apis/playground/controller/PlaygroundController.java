package apinexo.core.apis.playground.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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

    @RequestMapping(value = "/zillow/**", method = { RequestMethod.GET, RequestMethod.POST })
    public ResponseEntity<?> zillow(HttpServletRequest request, @RequestBody(required = false) String body) {
        String baseUrl = "http://45.63.16.213:8080/rapidapi/zillow/com";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String path = request.getRequestURI();
        String method = request.getMethod();
        String query = request.getQueryString();
        String url = baseUrl + path.replace("/zillow", "");
        if (query != null) {
            url += "?" + query;
        }
        return facade.forwardToThirdParty(headers, url, method, query, body);
    }
}
