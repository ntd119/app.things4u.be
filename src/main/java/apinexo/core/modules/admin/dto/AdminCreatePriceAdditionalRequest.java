package apinexo.core.modules.admin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminCreatePriceAdditionalRequest {

    @JsonProperty("api_id")
    private String apiId;

    @JsonProperty("api_name")
    private String apiName;

    @JsonProperty("up_to")
    private String upTo;

    @JsonProperty("price")
    private String price;
}