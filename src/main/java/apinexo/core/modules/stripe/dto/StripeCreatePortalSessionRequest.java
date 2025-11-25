package apinexo.core.modules.stripe.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class StripeCreatePortalSessionRequest {

    @JsonProperty("customer_id")
    private String customerId;
}
