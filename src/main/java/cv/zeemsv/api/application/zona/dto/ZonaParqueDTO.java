package cv.zeemsv.api.application.zona.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ZonaParqueDTO {
    private Integer id;
    private Integer idZona;
    private String nome;
    private String sigla;
    private String estado;
    private LocalDateTime dateCreate;
}
