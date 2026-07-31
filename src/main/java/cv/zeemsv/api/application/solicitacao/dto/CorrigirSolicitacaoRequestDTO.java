package cv.zeemsv.api.application.solicitacao.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CorrigirSolicitacaoRequestDTO {
    private String exposicao;
    private String idsLote;
    private Integer idProjeto;
    private List<SolicitacaoDocumentoRequestDTO> documentos = new ArrayList<>();
    private List<SolicitacaoRequisitoRequestDTO> requisitos = new ArrayList<>();

    public void setIds_lote(String idsLote) {
        this.idsLote = idsLote;
    }

    public void setId_projeto(Integer idProjeto) {
        this.idProjeto = idProjeto;
    }

    public void setId_projecto(Integer idProjeto) {
        this.idProjeto = idProjeto;
    }
}
