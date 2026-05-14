package cv.zeemsv.api.application.investidor.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class InvestidorRequestDTO {
    @NotBlank(message = "O campo nome é obrigatório")
    private String nome;
    private String descricao;
    private String estado;
}
