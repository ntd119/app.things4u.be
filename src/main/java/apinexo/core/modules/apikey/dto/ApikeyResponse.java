package apinexo.core.modules.apikey.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ApikeyResponse {
    @JsonProperty("apiKey")
    private String apiKey;
}