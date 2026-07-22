package cv.zeemsv.api.application.solicitacao.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
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
    private String exposicao;
    private String origem;
    private String email;
    private String nomeDenominacao;
    private String denominacaoSocial;
    private LocalDate dataPrevistaResposta;
    private Integer prazoDias;

    private Integer idUser;
    private String userName;
    private BigDecimal idOrganica;

    private List<SolicitacaoDocumentoRequestDTO> documentos = new ArrayList<>();
    private List<SolicitacaoRequisitoRequestDTO> requisitos = new ArrayList<>();
}
