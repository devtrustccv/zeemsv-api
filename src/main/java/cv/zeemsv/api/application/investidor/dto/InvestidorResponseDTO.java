package cv.zeemsv.api.application.investidor.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class InvestidorResponseDTO {
    private Integer id;
    private String nome;
    private String descricao;
    private String estado;
    private String paisOrigem;
}
