package cv.zeemsv.api.application.cobranca.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CobrancaPagamentoResponseDTO {
    private Integer id;
    private Integer idSolicitacao;
    private Integer idCobranca;
    private Integer idPrestacao;
    private Integer idTpSolicTaxa;
    private Integer idInvestidor;
    private Integer idPromotor;
    private Integer idProjeto;
    private Integer idProcesso;
    private BigDecimal valor;
    private String valorPago;
    private String nrProcesso;
    private String entidade;
    private String referencia;
    private String duc;
    private String dmEstadoPag;
    private String dmEstadoPagDesc;
    private LocalDate dataPagamento;
    private LocalDate dataRegisto;
    private String userRegisto;
    private String formaPagamento;
    private String origemPagamento;
    private String nrCheque;
    private String numCheque;
    private String flagIntegracao;
    private LocalDate dataIntegracao;
    private String userIntegracao;
    private String dmEstado;
    private String dmEstadoDesc;
    private String banco;
    private String linkDuc;
    private Integer idSolicTaxa;
    private String userPagamento;
    private BigDecimal idTaxa;
    private List<CobrancaTaxaResponseDTO> taxas;
}
