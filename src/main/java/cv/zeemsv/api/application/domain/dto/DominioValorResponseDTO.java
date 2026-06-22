package cv.zeemsv.api.application.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DominioValorResponseDTO {
    private String dominio;
    private String valor;
    private String description;
}
