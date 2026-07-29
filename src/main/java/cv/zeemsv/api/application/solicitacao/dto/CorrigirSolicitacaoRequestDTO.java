package cv.zeemsv.api.application.solicitacao.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CorrigirSolicitacaoRequestDTO {
    private List<SolicitacaoDocumentoRequestDTO> documentos = new ArrayList<>();
    private List<SolicitacaoRequisitoRequestDTO> requisitos = new ArrayList<>();
}
