package cv.zeemsv.api.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "zeemsv.integration.payment-gateway")
@Getter
@Setter
public class PaymentGatewayProperties {
    private boolean enabled = true;
    private String authTokenUrl;
    private String paymentUrl;
    private String paymentValidateUrl;
    private String grantType = "client_credentials";
    private String clientId;
    private String clientSecret;
    private long tokenExpirySkewSeconds = 30;
}
