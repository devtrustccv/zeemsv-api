package cv.zeemsv.api.application.paymentgateway.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentGatewayPaymentValidationRequestDTO {
    private String transactionId;
    private String status;
    private String channelCode;
    private String merchantRespErrorDescription;
    private String merchantRespMerchantRef;
    private String merchantRespMerchantSession;
    private String fingerprint;
}
