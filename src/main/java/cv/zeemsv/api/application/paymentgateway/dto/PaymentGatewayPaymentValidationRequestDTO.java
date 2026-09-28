package cv.zeemsv.api.application.paymentgateway.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentGatewayPaymentValidationRequestDTO {
    @NotBlank(message = "O campo transactionId e obrigatorio")
    private String transactionId;

    @NotBlank(message = "O campo status e obrigatorio")
    private String status;

    @NotBlank(message = "O campo channelCode e obrigatorio")
    private String channelCode;

    private String merchantRespErrorDescription;
    private String merchantRespMerchantRef;
    private String merchantRespMerchantSession;
    private String fingerprint;
}
