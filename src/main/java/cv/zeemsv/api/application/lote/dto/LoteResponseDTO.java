package cv.zeemsv.api.application.lote.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LoteResponseDTO {
    private Integer id;
    private String nome;
    private String descricao;
    private String estado;
    private JsonNode coordenadas;
}
