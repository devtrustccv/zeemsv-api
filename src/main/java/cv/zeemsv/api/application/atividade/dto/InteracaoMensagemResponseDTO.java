package cv.zeemsv.api.application.atividade.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class InteracaoMensagemResponseDTO {
    private Integer id;
    private Integer idInteracao;
    private Integer idRelacao;
    private String tpRelacao;
    private String mensagem;
    private Integer userEnvio;
    private LocalDateTime dataEnvio;
    private String pathDoc;
    private String urlDoc;
}
