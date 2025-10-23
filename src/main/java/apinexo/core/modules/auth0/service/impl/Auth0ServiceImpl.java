package apinexo.core.modules.auth0.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import apinexo.common.dtos.AbstractService;
import apinexo.common.utils.ApinexoUtils;
import apinexo.core.modules.auth0.request.client.Auth0GenerateTokenClientRequest;
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
    public JsonNode getUser(String sub) {
        JsonNode tokenObj = this.generateToken();
        String token = utils.jsonNodeAt(tokenObj, "/access_token", String.class);
        HttpHeaders headers = utils.buildHeader();
        headers.setBearerAuth(token);
        String url = audience + "/api/v2/users?q=user_id:\"" + sub + "\"&search_engine=v3";
        JsonNode response = executeGetRequest(JsonNode.class, url, null, headers);
        return response;
    }

    @Override
    public JsonNode generateToken() {
        Auth0GenerateTokenClientRequest body = Auth0GenerateTokenClientRequest.builder().clientId(clientId)
                .clientSecret(clientSecret).audience(audience + "/api/v2/").grantType("client_credentials").build();
        HttpHeaders headers = utils.buildHeader();
        String url = audience + "/oauth/token";
        JsonNode response = executePostRequest(JsonNode.class, url, utils.convertDtoToJson(body), headers);
        return response;
    }

}
