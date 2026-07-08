package cv.zeemsv.api.application.atividade.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class NotificacaoRespostaRequestDTO {
    @JsonProperty("id_pai")
    @Schema(name = "id_pai", example = "10")
    @NotNull(message = "O campo id_pai e obrigatorio")
    private Integer idPai;

    @JsonProperty("user_registo")
    @Schema(name = "user_registo", example = "5")
    @NotNull(message = "O campo user_registo e obrigatorio")
    private Integer userRegisto;

    @Schema(example = "Resposta da notificação")
    @Size(max = 255, message = "O campo assunto deve ter no maximo 255 caracteres")
    private String assunto;

    @Schema(example = "Mensagem da resposta")
    @NotBlank(message = "O campo mensagem e obrigatorio")
    @Size(max = 4000, message = "O campo mensagem deve ter no maximo 4000 caracteres")
    private String mensagem;
}
