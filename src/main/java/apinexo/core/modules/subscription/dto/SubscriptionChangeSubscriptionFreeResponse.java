package apinexo.core.modules.subscription.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import apinexo.core.modules.plans.dto.ApiPlansResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class SubscriptionChangeSubscriptionFreeResponse {

    @JsonProperty("id")
    private String id;

    @JsonProperty("customerId")
    private String customerId;

    @JsonProperty("plan")
    private ApiPlansResponse plan;

    @JsonProperty("activeFrom")
    private String activeFrom;

    @JsonProperty("activeTo")
    private String activeTo;
}
