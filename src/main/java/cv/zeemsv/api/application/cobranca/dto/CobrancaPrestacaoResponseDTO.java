package cv.zeemsv.api.application.cobranca.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CobrancaPrestacaoResponseDTO {
    private Integer id;
    private Integer idCobranca;
    private String nrPrestacao;
    private String valor;
    private LocalDate dataVencimento;
    private String dmEstado;
    private String dmEstadoDesc;
    private String userRegisto;
    private LocalDate dataRegisto;
    private List<CobrancaPagamentoResponseDTO> pagamentos;
}
