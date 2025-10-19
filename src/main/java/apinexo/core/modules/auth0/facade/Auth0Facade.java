package apinexo.core.modules.auth0.facade;

import com.fasterxml.jackson.databind.JsonNode;

public interface Auth0Facade {

    JsonNode generateToken();

    JsonNode getUser(String sub);
}
