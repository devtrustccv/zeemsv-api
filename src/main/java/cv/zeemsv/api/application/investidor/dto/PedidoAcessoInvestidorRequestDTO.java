package cv.zeemsv.api.application.investidor.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PedidoAcessoInvestidorRequestDTO {
    @NotNull(message = "O campo id_user e obrigatorio")
    private Integer idUser;

    @NotNull(message = "O campo id_investidor e obrigatorio")
    private Integer idInvestidor;

    private String dmTpRepresentante;
    private String dmEstado;
    private String obs;
}
