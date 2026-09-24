package cv.zeemsv.api.application.paymentgateway.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentGatewayPaymentResponseDTO {
    @JsonAlias({"intentionId", "intention_id"})
    private String intentionId;

    @JsonAlias({"paymentUrl", "payment_url"})
    private String paymentUrl;
}
