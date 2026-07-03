package cv.zeemsv.api.application.investidor.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SocioRepresentanteRequestDTO {
    @NotBlank(message = "O campo nome e obrigatorio")
    private String nome;

    private String nacionalidade;
    private String nif;
    private String tipoDoc;
    private String nrDoc;
    private BigDecimal telefone;
    private BigDecimal telemovel;

    @NotBlank(message = "O campo email e obrigatorio")
    @Email(message = "Email invalido")
    private String email;

    private String fotoUrl;
    private String indicativoPais;
}
