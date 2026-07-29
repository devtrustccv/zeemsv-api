package cv.zeemsv.api.application.solicitacao.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubmeterSolicitacaoRequestDTO {
    @NotNull(message = "O campo id_tp_solicitacao e obrigatorio")
    private Integer idTpSolicitacao;

    private Integer idPromotor;
    private Integer idInvestidor;
    private Integer idProjeto;
    private String idsLote;
    private String exposicao;
    private String origem;
    private String email;
    private String nomeRequerente;
    private LocalDate dataPrevistaResposta;
    private Integer prazoDias;

    private Integer idUser;
    private String userName;

    private List<SolicitacaoDocumentoRequestDTO> documentos = new ArrayList<>();
    private List<SolicitacaoRequisitoRequestDTO> requisitos = new ArrayList<>();

    public void setId_tp_solicitacao(Integer idTpSolicitacao) {
        this.idTpSolicitacao = idTpSolicitacao;
    }

    public void setId_promotor(Integer idPromotor) {
        this.idPromotor = idPromotor;
    }

    public void setId_investidor(Integer idInvestidor) {
        this.idInvestidor = idInvestidor;
    }

    public void setId_projeto(Integer idProjeto) {
        this.idProjeto = idProjeto;
    }

    public void setId_projecto(Integer idProjeto) {
        this.idProjeto = idProjeto;
    }

    public void setIds_lote(String idsLote) {
        this.idsLote = idsLote;
    }

    public void setNome_requerente(String nomeRequerente) {
        this.nomeRequerente = nomeRequerente;
    }

    public void setData_prevista_resposta(LocalDate dataPrevistaResposta) {
        this.dataPrevistaResposta = dataPrevistaResposta;
    }

    public void setPrazo_dias(Integer prazoDias) {
        this.prazoDias = prazoDias;
    }

    public void setId_user(Integer idUser) {
        this.idUser = idUser;
    }

    public void setUser_name(String userName) {
        this.userName = userName;
    }
}
