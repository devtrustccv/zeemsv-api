package cv.zeemsv.api.domain.investidor.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Investidor {
    private Integer id;
    private String nome;
    private String descricao;
    private String estado;
    private String paisOrigem;
}
