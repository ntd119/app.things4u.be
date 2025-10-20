package apinexo.core.modules.openmeter.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class OpenmeterOmTokenResponse {

    @JsonProperty("expiresAt")
    private String expiresAt;

    @JsonProperty("id")
    private String id;

    @JsonProperty("subject")
    private String subject;

    @JsonProperty("token")
    private String token;
}
