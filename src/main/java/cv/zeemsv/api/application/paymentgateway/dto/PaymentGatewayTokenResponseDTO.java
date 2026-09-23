package cv.zeemsv.api.application.paymentgateway.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentGatewayTokenResponseDTO {
    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("expires_in")
    private Long expiresIn;

    @JsonProperty("refresh_expires_in")
    private Long refreshExpiresIn;

    private String scope;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("not-before-policy")
    private Long notBeforePolicy;
}
