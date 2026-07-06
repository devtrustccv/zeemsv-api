package cv.zeemsv.api.application.atividade.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class InteracaoAnexoResponseDTO {
    private Integer id;
    private String path;
    private String url;
    private String nomeFicheiro;
    private String mimetype;
    private BigDecimal docSize;
    private String descricao;
    private LocalDateTime dateCreate;
}
