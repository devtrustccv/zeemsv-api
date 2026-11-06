package cv.zeemsv.api.application.cobranca.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ComprovativoPagamentoResponseDTO {
    private Integer idCobranca;
    private Integer idPagamento;
    private Integer idPrestacao;
    private String dmEstadoPag;
    private String pathComprovativoPagamento;
}
