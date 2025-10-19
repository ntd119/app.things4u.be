package apinexo.core.modules.auth0.facade.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;

import apinexo.common.dtos.AbstractService;
import apinexo.common.utils.ApinexoUtils;
import apinexo.core.modules.auth0.facade.Auth0Facade;
import apinexo.core.modules.auth0.request.client.Auth0GenerateTokenClientRequest;

@Component
public class Auth0FacadeImpl extends AbstractService implements Auth0Facade {

    @Autowired
    private ApinexoUtils utils;

    @Value("${auth0.client-id}")
    private String clientId;

    @Value("${auth0.client-secret}")
    private String clientSecret;

    @Value("${auth0.auth0.audience}")
    private String audience;

    @Override
    public JsonNode generateToken() {
        Auth0GenerateTokenClientRequest body = Auth0GenerateTokenClientRequest.builder().clientId(clientId)
                .clientSecret(clientSecret).audience(audience).grantType("client_credentials").build();
        HttpHeaders headers = utils.buildHeader();
        String url = "https://openmeter.cloud/api/v1/portal/tokens";
        JsonNode response = executePostRequest(JsonNode.class, url, body, headers);
        return response;
    }
}
