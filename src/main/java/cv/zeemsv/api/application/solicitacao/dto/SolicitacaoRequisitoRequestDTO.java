package cv.zeemsv.api.application.solicitacao.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SolicitacaoRequisitoRequestDTO {
    private Integer idTpSolicTpDoc;
    private String cumpre;
    private String cumpreCheck;
}
