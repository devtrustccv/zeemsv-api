package cv.zeemsv.api.application.atividade.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class NotificacaoRespostaResponseDTO {
    private Integer id;
    private Integer idPai;
    private String assunto;
    private String mensagem;
    private String emailsEnviados;
    private String flagSucesso;
    private LocalDateTime dataEnvio;
}
