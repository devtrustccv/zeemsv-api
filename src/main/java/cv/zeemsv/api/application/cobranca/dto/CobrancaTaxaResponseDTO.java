package cv.zeemsv.api.application.cobranca.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CobrancaTaxaResponseDTO {
    private Integer id;
    private Integer idCobranca;
    private Integer idPagamento;
    private Integer idSolicTaxa;
    private Integer idTpSolicTaxa;
    private Integer idTaxa;
    private Integer idTaxaCond;
    private String codigoTaxa;
    private String refFin;
    private String descricao;
    private String tipoTaxa;
    private String tipoTaxaDesc;
    private BigDecimal valor;
    private BigDecimal valorConfigurado;
    private String dmEstado;
    private String dmEstadoDesc;
    private String userRegisto;
    private LocalDate dataRegisto;
}
