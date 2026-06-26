package cv.zeemsv.api.application.domain.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DominioValoresResponseDTO {
    private String dominio;
    private List<DominioValorResponseDTO> valores;
}
