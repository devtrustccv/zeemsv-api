package cv.zeemsv.api.application.investidor.dto;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SocioRepresentanteRequestDTO {
    @NotBlank(message = "O campo nome e obrigatorio")
    private String nome;

    private String nacionalidade;
    private String nif;
    private String tipoDoc;
    private String nrDoc;
    private BigDecimal telefone;
    private BigDecimal telemovel;
    private String email;
    private String estado;
    private String indicativoPais;
}
