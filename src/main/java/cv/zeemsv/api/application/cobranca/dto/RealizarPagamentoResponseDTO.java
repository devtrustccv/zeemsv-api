package cv.zeemsv.api.application.cobranca.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RealizarPagamentoResponseDTO {
    private String intentionId;
    private String linkPayment;
    private LocalDateTime expiresAt;
    private long expiresInSeconds;
}
