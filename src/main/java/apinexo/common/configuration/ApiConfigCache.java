package apinexo.common.configuration;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;

import apinexo.common.utils.ApinexoUtils;
import apinexo.common.utils.ConstantUtils;
import jakarta.annotation.PostConstruct;

@Component
public class ApiConfigCache {

    private JsonNode apis;

    @Autowired
    private ApinexoUtils utils;

    @PostConstruct
    public void init() {
        try {
            this.apis = utils.readJsonFile(ConstantUtils.API_CONFIG_PATH, JsonNode.class);
        } catch (IOException e) {
        }
    }

    public JsonNode getApiConfig() {
        return this.apis;
    }

    public void setApiConfig(JsonNode api) {
        this.apis = api;
    }

    public JsonNode getApiByPrefix(String prefix) {
        return utils.getJsonInList(apis, "id", prefix);
    }
}
