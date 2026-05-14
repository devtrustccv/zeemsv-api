package cv.zeemsv.api.application.lote.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LoteRequestDTO {
    @NotBlank(message = "O campo nome é obrigatório")
    private String nome;
    private String descricao;
    private String estado;
}
