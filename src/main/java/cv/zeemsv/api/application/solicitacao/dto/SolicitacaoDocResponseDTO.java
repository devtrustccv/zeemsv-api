package cv.zeemsv.api.application.solicitacao.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SolicitacaoDocResponseDTO {
    private Integer id;
    private Integer idSolicitacao;
    private Integer idTpSolicTpDoc;
    private Integer idTpDoc;
    private String tpDocNome;
    private String tpDocCodigo;
    private String requisito;
    private String flagObrigatorio;
    private String pedResp;
    private BigDecimal idProcesso;
    private BigDecimal idEtapa;
    private LocalDate dataRegisto;
    private String userRegisto;
    private String path;
}
