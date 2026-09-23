package cv.zeemsv.api.application.cobranca.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RealizarPagamentoResponseDTO {
    private String intentionId;
    private String linkPayment;
}
