package cv.zeemsv.api.domain.lote.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Lote {
    private Integer id;
    private String nome;
    private String descricao;
    private String estado;
    private JsonNode coordenadas;
}
