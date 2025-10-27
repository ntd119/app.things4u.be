package apinexo.core.modules.plans.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ApiPlansResponse {

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
    private Boolean active;

    @JsonProperty("price")
    private Integer price;

    @JsonProperty("is_free")
    private Boolean isFree;

    @JsonProperty("overage_prices")
    private List<String> overagePrices;

    @JsonProperty("metadata")
    private JsonNode metadata;
}
