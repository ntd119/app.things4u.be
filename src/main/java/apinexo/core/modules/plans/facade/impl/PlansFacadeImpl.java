package apinexo.core.modules.plans.facade.impl;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import apinexo.common.dtos.AbstractService;
import apinexo.common.utils.ApinexoUtils;
import apinexo.core.modules.plans.facade.PlansFacade;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PlansFacadeImpl extends AbstractService implements PlansFacade {

    private final ApinexoUtils utils;

    @Override
    public ResponseEntity<Object> plans(Jwt jwt, String id) {
        try {
            String json = "[\r\n" + "    {\r\n" + "        \"id\": \"68b948cbddae44eacad9317c\",\r\n"
                    + "        \"nickname\": \"Basic\",\r\n" + "        \"key\": \"basic\",\r\n"
                    + "        \"up_to\": 200,\r\n" + "        \"period\": \"month\",\r\n"
                    + "        \"currency\": null,\r\n" + "        \"active\": true,\r\n"
                    + "        \"price\": null,\r\n" + "        \"is_free\": true,\r\n"
                    + "        \"overage_prices\": [],\r\n" + "        \"metadata\": {\r\n"
                    + "            \"key\": \"basic\",\r\n" + "            \"rate_limit\": 1000,\r\n"
                    + "            \"rate_limit_period\": \"hour\",\r\n" + "            \"is_soft_limit\": false,\r\n"
                    + "            \"private\": false,\r\n" + "            \"api_id\": \"jsearch\"\r\n"
                    + "        }\r\n" + "    },\r\n" + "    {\r\n"
                    + "        \"id\": \"price_1S3YCmRoRAtc95u1thyjr81b\",\r\n" + "        \"nickname\": \"Pro\",\r\n"
                    + "        \"key\": \"pro\",\r\n" + "        \"up_to\": 10000,\r\n"
                    + "        \"period\": \"month\",\r\n" + "        \"currency\": \"usd\",\r\n"
                    + "        \"active\": true,\r\n" + "        \"price\": 2500,\r\n"
                    + "        \"is_free\": false,\r\n" + "        \"overage_prices\": [\r\n"
                    + "            \"0.3\"\r\n" + "        ],\r\n" + "        \"metadata\": {\r\n"
                    + "            \"api_id\": \"jsearch\",\r\n" + "            \"is_soft_limit\": \"true\",\r\n"
                    + "            \"key\": \"pro\",\r\n" + "            \"private\": \"false\",\r\n"
                    + "            \"rate_limit\": \"5\",\r\n" + "            \"rate_limit_period\": \"second\"\r\n"
                    + "        }\r\n" + "    },\r\n" + "    {\r\n"
                    + "        \"id\": \"price_1S3YCmRoRAtc95u19GnkLda0\",\r\n" + "        \"nickname\": \"Ultra\",\r\n"
                    + "        \"key\": \"ultra\",\r\n" + "        \"up_to\": 50000,\r\n"
                    + "        \"period\": \"month\",\r\n" + "        \"currency\": \"usd\",\r\n"
                    + "        \"active\": true,\r\n" + "        \"price\": 7500,\r\n"
                    + "        \"is_free\": false,\r\n" + "        \"overage_prices\": [\r\n"
                    + "            \"0.2\"\r\n" + "        ],\r\n" + "        \"metadata\": {\r\n"
                    + "            \"api_id\": \"jsearch\",\r\n" + "            \"is_soft_limit\": \"true\",\r\n"
                    + "            \"key\": \"ultra\",\r\n" + "            \"private\": \"false\",\r\n"
                    + "            \"rate_limit\": \"10\",\r\n" + "            \"rate_limit_period\": \"second\"\r\n"
                    + "        }\r\n" + "    },\r\n" + "    {\r\n"
                    + "        \"id\": \"price_1S3YCnRoRAtc95u1LW4pDadp\",\r\n" + "        \"nickname\": \"Mega\",\r\n"
                    + "        \"key\": \"mega\",\r\n" + "        \"up_to\": 200000,\r\n"
                    + "        \"period\": \"month\",\r\n" + "        \"currency\": \"usd\",\r\n"
                    + "        \"active\": true,\r\n" + "        \"price\": 15000,\r\n"
                    + "        \"is_free\": false,\r\n" + "        \"overage_prices\": [\r\n"
                    + "            \"0.1\"\r\n" + "        ],\r\n" + "        \"metadata\": {\r\n"
                    + "            \"api_id\": \"jsearch\",\r\n" + "            \"is_soft_limit\": \"true\",\r\n"
                    + "            \"key\": \"mega\",\r\n" + "            \"private\": \"false\",\r\n"
                    + "            \"rate_limit\": \"20\",\r\n" + "            \"rate_limit_period\": \"second\"\r\n"
                    + "        }\r\n" + "    }\r\n" + "]";
            return ResponseEntity.ok(utils.convertStrToJson(json));
        } catch (HttpClientErrorException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(utils.err(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(utils.err(ex.getMessage()));
        }
    }
}
