package cv.zeemsv.api.application.investidor.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PedidoAcessoInvestidorResponseDTO {
    private Integer id;
    private Integer idUser;
    private Integer idInvestidor;
    private String dmTpRepresentante;
    private String ficheiroCompravativo;
    private byte[] ficheiroCompravativoBytes;
    private String dmEstado;
    private String obs;
    private LocalDate dataRegisto;
    private LocalDate dataResposta;
    private String userResposta;
}
