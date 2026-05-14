package cv.zeemsv.api.domain.projeto.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Projeto {
    private Integer id;
    private String nome;
    private String descricao;
    private String estado;
}
