package apinexo.core.modules.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class UserResponse {
    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("email")
    private String email;

    @JsonProperty("email_verified")
    private Boolean emailVerified;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("company")
    private String company;

    @JsonProperty("picture")
    private String picture;

    @JsonProperty("auth0_user_id")
    private String auth0UserId;

    @JsonProperty("openmeter_customer_id")
    private String openmeterCustomerId;

    @JsonProperty("stripe_customer_id")
    private String stripeCustomerId;
}