package apinexo.core.modules.plans.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ApiPlansResponse {

    @Id
    @JsonProperty("id")
    private String id;

    @JsonProperty("basic")
    private JsonNode basic;

    @JsonProperty("pro")
    private JsonNode pro;

    @JsonProperty("ultra")
    private JsonNode ultra;

    @JsonProperty("mega")
    private JsonNode mega;
}
