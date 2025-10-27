package apinexo.core.modules.admin.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminCreateApiRequest {

    private String id;
    private String name;

    @JsonProperty("short_description")
    private String shortDescription;

    @JsonProperty("long_description")
    private String longDescription;

    private List<PlanDTO> plans;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PlanDTO {
        private String nickname;
        private String key;

        @JsonProperty("up_to")
        private Long upTo;

        private String period;
        private String currency;
        private Boolean active;
        private Integer price;

        @JsonProperty("is_free")
        private Boolean isFree;

        @JsonProperty("overage_prices")
        private List<Object> overagePrices;

        private Metadata metadata;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class Metadata {
            @JsonProperty("rate_limit")
            private String rateLimit;

            @JsonProperty("rate_limit_period")
            private String rateLimitPeriod;

            @JsonProperty("is_soft_limit")
            private String isSoftLimit;

            @JsonProperty("private")
            private String _private;
        }
    }
}