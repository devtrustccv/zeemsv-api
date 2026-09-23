package cv.zeemsv.api.application.cobranca.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RealizarPagamentoRequestDTO {
    @NotEmpty(message = "O campo ids_cobranca e obrigatorio")
    private List<@NotNull(message = "O campo ids_cobranca nao pode conter valores nulos") Integer> idsCobranca;
}
