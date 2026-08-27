package cv.zeemsv.api.application.solicitacao.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SolicitacaoTaxaResponseDTO {
    private Integer id;
    private Integer idSolicitacao;
    private Integer idTpSolicTaxa;
    private Integer idTaxa;
    private String taxa;
    private String tipoTaxaCodigo;
    private String tipoTaxa;
    private BigDecimal valor;
    private BigDecimal valorConfigurado;
    private Boolean instantPagamento;
    private Integer idPagamento;
    private String referenciaPagamento;
    private String duc;
    private String dmEstadoPagamento;
    private String dmEstadoPagamentoDesc;
    private String formaPagamento;
    private String linkDuc;
}
