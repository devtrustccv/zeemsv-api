package cv.zeemsv.api.application.paymentgateway.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentGatewayPaymentRequestDTO {
    @NotBlank(message = "O campo channelCode e obrigatorio")
    private String channelCode;

    @NotBlank(message = "O campo transactionId e obrigatorio")
    private String transactionId;

    @NotNull(message = "O campo total e obrigatorio")
    @DecimalMin(value = "0.01", message = "O campo total deve ser maior que zero")
    private BigDecimal total;

    @NotBlank(message = "O campo paymentType e obrigatorio")
    private String paymentType;

    @NotBlank(message = "O campo email e obrigatorio")
    @Email(message = "O campo email deve ser valido")
    private String email;

    @NotBlank(message = "O campo billAddrCountry e obrigatorio")
    private String billAddrCountry;

    @NotBlank(message = "O campo billAddrCity e obrigatorio")
    private String billAddrCity;

    @NotBlank(message = "O campo billAddrLine1 e obrigatorio")
    private String billAddrLine1;

    @NotBlank(message = "O campo billAddrPostCode e obrigatorio")
    private String billAddrPostCode;
}
