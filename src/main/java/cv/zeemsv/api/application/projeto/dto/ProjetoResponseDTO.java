package cv.zeemsv.api.application.projeto.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ProjetoResponseDTO {
    private Integer id;
    private String nome;
    private String descricao;
    private String estado;
}
