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
public class OpenmeterSendEventClientRequest {

    @JsonProperty("specversion")
    private String specversion;

    @JsonProperty("type")
    private String type;

    @JsonProperty("id")
    private String id;

    @JsonProperty("time")
    private String time;

    @JsonProperty("source")
    private String source;

    @JsonProperty("subject")
    private String subject;

    @JsonProperty("data")
    private DataContent data;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DataContent {

        @JsonProperty("value")
        private String value;

        @JsonProperty("method")
        private String method;

        @JsonProperty("route")
        private String route;
    }

}
