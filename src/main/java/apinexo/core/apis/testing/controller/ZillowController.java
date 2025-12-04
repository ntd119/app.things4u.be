package apinexo.core.apis.testing.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import apinexo.core.apis.testing.facade.CommonFacade;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/zillow")
public class ZillowController {

    private final CommonFacade facade;

    private final String BASE_URL = "http://45.63.16.213:8080/rapidapi/zillow/com";

    @RequestMapping(value = "/**", method = { RequestMethod.GET, RequestMethod.POST })
    public ResponseEntity<?> handleDynamicApi(HttpServletRequest request, @RequestBody(required = false) String body) {
        String path = request.getRequestURI();
        String method = request.getMethod();
        String query = request.getQueryString();
        String url = BASE_URL + path.replace("/zillow", "");
        if (query != null) {
            url += "?" + query;
        }
        return facade.forwardToThirdParty(url, method, query, body);
    }
}
