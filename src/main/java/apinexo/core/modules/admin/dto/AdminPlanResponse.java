package apinexo.core.modules.admin.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class AdminPlanResponse {
    @JsonProperty("id")
    private String id;

    @JsonProperty("nickname")
    private String nickname;

    @JsonProperty("key")
    private String key;

    @JsonProperty("up_to")
    private Long upTo;

    @JsonProperty("period")
    private String period;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("active")
    private boolean active;

    @JsonProperty("price")
    private Integer price;

    @JsonProperty("is_free")
    private boolean isFree;

    @JsonProperty("overage_prices")
    private List<String> overagePrices;

    @JsonProperty("metadata")
    private Metadata metadata;

    @Data
    @AllArgsConstructor
    @Builder
    public static class Metadata {
        @JsonProperty("api_id")
        private String apiId;

        @JsonProperty("is_soft_limit")
        private String isSoftLimit;

        @JsonProperty("key")
        private String key;

        @JsonProperty("private")
        private String privateField;

        @JsonProperty("rate_limit")
        private String rateLimit;

        @JsonProperty("rate_limit_period")
        private String rateLimitPeriod;
    }
}
