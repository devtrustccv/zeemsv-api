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
public class CobrancaInvestidorResponseDTO {
    private Integer id;
    private Integer nrCobranca;
    private Integer idInvestidor;
    private Integer idProjeto;
    private Integer nrProcesso;
    private List<Integer> idsSolicitacao;
    private Integer idSolicitacao;
    private Integer idSolicTaxa;
    private LocalDate dataEmissao;
    private LocalDate dataVencimento;
    private String valorTotal;
    private String valorPago;
    private String valorDivida;
    private String tipoLiquidacao;
    private Integer nrPrestacao;
    private String dmEstado;
    private String dmEstadoDesc;
    private String userRegisto;
    private LocalDate dataRegisto;
    private CobrancaTaxaResponseDTO taxa;
    private List<CobrancaPrestacaoResponseDTO> prestacoes;
    private List<CobrancaPagamentoResponseDTO> pagamentos;
}
