package cv.zeemsv.api.application.investidor.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.Email;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SocioRepresentanteUpdateRequestDTO {
    private BigDecimal telefone;
    private BigDecimal telemovel;
    private String indicativoPais;

    @Email(message = "Email invalido")
    private String email;
}
