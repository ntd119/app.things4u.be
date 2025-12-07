package apinexo.core.apis.playground.controller;

import org.apache.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import apinexo.core.apis.playground.entity.ApiProxyConfig;
import apinexo.core.apis.playground.facade.PlaygroundFacade;
import apinexo.core.apis.playground.service.ApiConfigService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/proxy")
@RequiredArgsConstructor
public class PlaygroundController {

    private final ApiConfigService apiConfigService;
    private final PlaygroundFacade facade;

    @RequestMapping(value = "/**", method = { RequestMethod.GET, RequestMethod.POST })
    public ResponseEntity<?> dynamicProxy(
            HttpServletRequest request,
            @RequestBody(required = false) String body) {

        String requestPath = request.getRequestURI();          // /proxy/zillow/search
        String dynamicPath = requestPath.replace("/proxy", ""); // /zillow/search

        // Tìm config matching prefix (/zillow, /redfin...)
        ApiProxyConfig config = apiConfigService.findConfig(dynamicPath);

        if (config == null) {
            return ResponseEntity.status(HttpStatus.SC_NOT_FOUND)
                    .body("API config not found for: " + dynamicPath);
        }

        String baseUrl = config.getTargetBaseUrl();            // lấy từ DB
        String prefix = config.getPrefix();                    // /zillow
        String forwardPath = dynamicPath.replace(prefix, "");

        String query = request.getQueryString();
        String method = request.getMethod();

        String finalUrl = baseUrl + forwardPath;
        if (query != null) {
            finalUrl += "?" + query;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return facade.forwardToThirdParty(headers, finalUrl, method, query, body);
    }
}
