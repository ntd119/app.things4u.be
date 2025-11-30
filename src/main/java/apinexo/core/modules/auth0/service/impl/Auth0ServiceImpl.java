package apinexo.core.modules.auth0.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.databind.JsonNode;

import apinexo.common.dtos.AbstractService;
import apinexo.common.utils.ApinexoUtils;
import apinexo.core.modules.auth0.service.Auth0Service;

@Service
public class Auth0ServiceImpl extends AbstractService implements Auth0Service {

    @Autowired
    private ApinexoUtils utils;

    @Value("${auth0.client-id}")
    private String clientId;

    @Value("${auth0.client-secret}")
    private String clientSecret;

    @Value("${auth0.audience}")
    private String audience;

    @Override
    public ResponseEntity<JsonNode> getUser(String sub) {
        JsonNode tokenObj = this.generateToken().getBody();
        String token = utils.jsonNodeAt(tokenObj, "/access_token", String.class);
        HttpHeaders headers = utils.buildHeader();
        headers.setBearerAuth(token);
        String url = audience + "/api/v2/users?q=user_id:\"" + sub + "\"&search_engine=v3";
        ResponseEntity<String> response = executeGetRequest(String.class, url, null, headers);
        JsonNode json = utils.convertStrToJson(response.getBody());
        return ResponseEntity.status(response.getStatusCode()).body(json);
    }

    @Override
    public ResponseEntity<JsonNode> generateToken() {
        HttpHeaders headers = utils.buildHeader();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("audience", audience + "/api/v2/");
        String url = audience + "/oauth/token";
        ResponseEntity<String> response = executePostRequest(String.class, url, body, headers);
        JsonNode json = utils.convertStrToJson(response.getBody());
        return ResponseEntity.status(response.getStatusCode()).body(json);
    }

    @Override
    public ResponseEntity<JsonNode> getUserByEmail(String email) {
        JsonNode tokenObj = this.generateToken().getBody();
        String token = utils.jsonNodeAt(tokenObj, "/access_token", String.class);
        HttpHeaders headers = utils.buildHeader();
        headers.setBearerAuth(token);
        String url = audience + "/api/v2/users-by-email?email=" + email;
        ResponseEntity<JsonNode> response = executeGetRequest(JsonNode.class, url, null, headers);
        return response;
    }

    @Override
    public ResponseEntity<JsonNode> resendVerificationEmail(String userId) {
        JsonNode tokenObj = this.generateToken().getBody();
        String token = utils.jsonNodeAt(tokenObj, "/access_token", String.class);
        HttpHeaders headers = utils.buildHeader();
        headers.setBearerAuth(token);
        String url = "https://login.apinexo.com/api/v2/jobs/verification-email";
        Map<String, String> body = new HashMap<>();
        body.put("user_id", userId);
        ResponseEntity<JsonNode> response = executePostRequest(JsonNode.class, url, body, headers);
        return response;
    }

    @Override
    public ResponseEntity<String> changePassword(String email) {
        JsonNode tokenObj = this.generateToken().getBody();
        String token = utils.jsonNodeAt(tokenObj, "/access_token", String.class);
        HttpHeaders headers = utils.buildHeader();
        headers.setBearerAuth(token);
        String url = audience + "/dbconnections/change_password";
        Map<String, String> body = new HashMap<>();
        body.put("client_id", clientId);
        body.put("email", email);
        body.put("connection", "Username-Password-Authentication");
        ResponseEntity<String> response = executePostRequest(String.class, url, body, headers);
        return response;
    }
}
