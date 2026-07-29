package cv.zeemsv.api.application.solicitacao.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SolicitacaoRequisitoRequestDTO {
    private Integer idTpSolicTpDoc;
    private String cumpre;

    public void setId_tp_solic_tp_doc(Integer idTpSolicTpDoc) {
        this.idTpSolicTpDoc = idTpSolicTpDoc;
    }
}
