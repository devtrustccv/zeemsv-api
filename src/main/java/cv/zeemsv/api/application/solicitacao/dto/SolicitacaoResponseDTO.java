package cv.zeemsv.api.application.solicitacao.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SolicitacaoResponseDTO {
    private Integer id;
    private String nome;
    private String descricao;
    private String estado;
}
