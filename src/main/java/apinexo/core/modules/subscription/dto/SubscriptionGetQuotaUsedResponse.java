package apinexo.core.modules.subscription.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class SubscriptionGetQuotaUsedResponse {

    @JsonProperty("total")
    private long total;
}