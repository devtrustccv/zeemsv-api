package cv.zeemsv.api.application.projeto.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ProjetoRequestDTO {
    @NotBlank(message = "O campo nome é obrigatório")
    private String nome;
    private String descricao;
    private String estado;
}
