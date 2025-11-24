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

    @JsonProperty("billing_period_from")
    private Long billingPeriodFrom;

    @JsonProperty("billing_period_to")
    private Long billingPeriodTo;

    @JsonProperty("current_plan")
    private String currentPlan;

    @JsonProperty("quota")
    private Long quota;

    @JsonProperty("period")
    private String period;

    @JsonProperty("quota_used")
    private Long quotaUsed;

    @JsonProperty("rate_limit")
    private Long rateLimit;

    @JsonProperty("rate_limit_period")
    private String rateLimitPeriod;

    @JsonProperty("is_soft_limit")
    private Boolean isSoftLimit;

    @JsonProperty("overage_prices")
    private Double overagePrices;

    @JsonProperty("price")
    private Double price;

    @JsonProperty("plan")
    private ApiPlansResponse plan;
}
