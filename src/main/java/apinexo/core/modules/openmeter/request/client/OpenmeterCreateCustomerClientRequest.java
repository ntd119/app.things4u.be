package apinexo.core.modules.openmeter.request.client;

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
public class OpenmeterCreateCustomerClientRequest {

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("key")
    private String key;

    @JsonProperty("usageAttribution")
    private UsageAttribution usageAttribution;

    @JsonProperty("primaryEmail")
    private String primaryEmail;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("billingAddress")
    private BillingAddress billingAddress;

    // ------- Inner Class: UsageAttribution -------
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UsageAttribution {

        @JsonProperty("subjectKeys")
        private java.util.List<String> subjectKeys;
    }

    // ------- Inner Class: BillingAddress -------
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BillingAddress {

        @JsonProperty("country")
        private String country;

        @JsonProperty("postalCode")
        private String postalCode;

        @JsonProperty("state")
        private String state;

        @JsonProperty("city")
        private String city;

        @JsonProperty("line1")
        private String line1;

        @JsonProperty("line2")
        private String line2;

        @JsonProperty("phoneNumber")
        private String phoneNumber;
    }
}
