package cv.zeemsv.api.application.cobranca.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RealizarPagamentoRequestDTO {
    @NotNull(message = "O campo id_cobranca e obrigatorio")
    private Integer idCobranca;

    @NotNull(message = "O campo valor e obrigatorio")
    @DecimalMin(value = "0.01", message = "O campo valor deve ser maior que zero")
    private BigDecimal valor;
}
