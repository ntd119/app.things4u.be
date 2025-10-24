package apinexo.core.modules.openmeter.request.client;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OpenmeterStripeCheckoutSessionsClientRequest {

    @JsonProperty("customer")
    private Customer customer;

    @JsonProperty("plan")
    private Plan plan;

    @JsonProperty("options")
    private Options options;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Customer {

        @JsonProperty("key")
        private String key;

        @JsonProperty("usageAttribution")
        private UsageAttribution usageAttribution;

        @Data
        @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class UsageAttribution {

            @JsonProperty("subjectKeys")
            private List<String> subjectKeys;
        }
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Plan {

        @JsonProperty("key")
        private String key;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Options {

        @JsonProperty("successUrl")
        private String successUrl;

        @JsonProperty("currency")
        private String currency;
    }
}
