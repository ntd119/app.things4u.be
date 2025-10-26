package apinexo.core.modules.subscription.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class SubscriptionChangeSubscriptionRequest {

    @JsonProperty("apiId")
    private String apiId;

    @JsonProperty("planKey")
    private String planKey;

    @JsonProperty("url")
    private String url;
}