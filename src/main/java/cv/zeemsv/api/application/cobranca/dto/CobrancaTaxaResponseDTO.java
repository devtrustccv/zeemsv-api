package cv.zeemsv.api.application.cobranca.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CobrancaTaxaResponseDTO {
    private Integer idSolicTaxa;
    private Integer idTpSolicTaxa;
    private Integer idTaxa;
    private String codigoTaxa;
    private String refFin;
    private String descricao;
    private String tipoTaxa;
    private String tipoTaxaDesc;
    private BigDecimal valor;
    private BigDecimal valorConfigurado;
}
