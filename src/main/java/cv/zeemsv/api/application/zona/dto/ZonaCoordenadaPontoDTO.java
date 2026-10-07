package cv.zeemsv.api.application.zona.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ZonaCoordenadaPontoDTO {
    private Integer id;
    private BigDecimal vertice;
    private String coordenadaX;
    private String coordenadaY;
    private String dmEstado;
    private LocalDate dataRegisto;
}
