package apinexo.core.modules.auth0.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Auth0ResendVerificationRequest {

    @JsonProperty("email")
    private String email;
}
