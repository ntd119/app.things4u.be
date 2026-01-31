package apinexo.core.modules.subscription.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionCached {

    @JsonProperty("id")
    private String id;

    @JsonProperty("apiId")
    private String apiId;

    @JsonProperty("billing_period_from")
    private Long billingPeriodFrom;

    @JsonProperty("billing_period_to")
    private Long billingPeriodTo;

    @JsonProperty("active")
    private boolean active;

    @JsonProperty("is_free")
    private boolean isFree;

    @JsonProperty("current_plan")
    private String currentPlan;

    @JsonProperty("quota")
    private Long quota;

    @JsonProperty("period")
    private String period;

    @JsonProperty("quota_used")
    private long quotaUsed;

    @JsonProperty("rate_limit")
    private Long rateLimit;

    @JsonProperty("rate_limit_period")
    private String rateLimitPeriod;

    @JsonProperty("is_soft_limit")
    private Boolean isSoftLimit;

    @JsonProperty("overage_prices")
    private Double overagePrices;

    @JsonProperty("is_rate_limit")
    private Boolean isRateLimit;

    @JsonProperty("price")
    private Double price;

    @JsonProperty("subscription_item_id")
    private String subscriptionItemId;
}
