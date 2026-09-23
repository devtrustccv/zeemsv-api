package cv.zeemsv.api.application.paymentgateway.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentGatewayPaymentResponseDTO {
    private String intentionId;
    private String paymentUrl;
}
