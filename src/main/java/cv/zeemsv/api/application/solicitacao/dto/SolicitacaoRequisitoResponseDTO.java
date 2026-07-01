package cv.zeemsv.api.application.solicitacao.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SolicitacaoRequisitoResponseDTO {
    private Integer idTpSolicTpDoc;
    private String requisito;
    private String flagObrigatorio;
    private String flagObrigatorioDesc;
    private String cumpre;
    private String cumpreCheck;
}
