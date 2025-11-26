package apinexo.core.modules.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class UserGetUserResponse {
    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("email")
    private String email;

    @JsonProperty("email_verified")
    private Boolean emailVerified;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("api_key")
    private String api_key;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("user_name")
    private String userName;

    @JsonProperty("picture")
    private String picture;

    @JsonProperty("auth0_user_id")
    private String auth0UserId;

    @JsonProperty("stripe_customer_id")
    private String stripeCustomerId;
}