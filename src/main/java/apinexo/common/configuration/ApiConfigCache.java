package apinexo.common.configuration;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;

import apinexo.common.utils.ApinexoUtils;
import jakarta.annotation.PostConstruct;

@Component
public class ApiConfigCache {

    private JsonNode apis;

    @Autowired
    private ApinexoUtils utils;

    @PostConstruct
    public void init() {
        try {
            this.apis = utils.readJsonFile("/data_static/api-config.json", JsonNode.class);
        } catch (IOException e) {
        }
    }

    public JsonNode getApiByPrefix(String prefix) {
        return utils.getJsonInList(apis, "id", prefix);
    }

}
