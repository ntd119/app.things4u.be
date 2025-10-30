package apinexo.core.modules.subscription.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import apinexo.core.modules.plans.dto.ApiPlansResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class SubscriptionResponse {

    @JsonProperty("api_id")
    private String apiId;

    @JsonProperty("api_name")
    private String apiName;

    @JsonProperty("subscription_id")
    private String subscriptionId;

    @JsonProperty("image")
    private String image;

    @JsonProperty("plan")
    private ApiPlansResponse plan;
}
