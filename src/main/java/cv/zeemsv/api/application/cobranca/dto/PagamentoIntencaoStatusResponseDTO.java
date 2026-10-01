package cv.zeemsv.api.application.cobranca.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PagamentoIntencaoStatusResponseDTO {
    private String intentionId;
    private String transactionId;
    private String estado;
    private boolean finalizado;
    private boolean pago;
    private boolean expirado;
    private String linkPayment;
    private BigDecimal valorTotal;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private long remainingSeconds;
    private List<Integer> idsCobranca;
    private List<Integer> idsPagamento;
}
