package apinexo.core.modules.auth0.service;

import com.fasterxml.jackson.databind.JsonNode;

public interface Auth0Service {

    JsonNode getUser(String sub);

    JsonNode generateToken();
}
