package apinexo.core.modules.subscription.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class SubscriptionChangeSubscriptionResponse {

    @JsonProperty("customerId")
    private String customerId;

    @JsonProperty("sessionId")
    private String sessionId;

    @JsonProperty("url")
    private String url;
}