package apinexo.core.modules.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import apinexo.core.modules.plans.dto.ApiPlansResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ApiResponse {

    @JsonProperty("api_id")
    private String apiId;

    @JsonProperty("plan")
    private ApiPlansResponse plan;
}
