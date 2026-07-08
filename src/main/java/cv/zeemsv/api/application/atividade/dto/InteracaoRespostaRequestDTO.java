package cv.zeemsv.api.application.atividade.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class InteracaoRespostaRequestDTO {
    @JsonProperty("user_resposta")
    @Schema(name = "user_resposta", example = "5")
    @NotNull(message = "O campo user_resposta e obrigatorio")
    private Integer userResposta;

    @Schema(example = "Resposta enviada ao utilizador")
    @NotBlank(message = "O campo mensagem e obrigatorio")
    @Size(max = 4000, message = "O campo mensagem deve ter no maximo 4000 caracteres")
    private String mensagem;

    @JsonProperty("path_doc")
    @Schema(name = "path_doc", nullable = true, example = "2026/modulos/INTERACAO_RESPOSTA/10/resposta.pdf")
    @Size(max = 1000, message = "O campo path_doc deve ter no maximo 1000 caracteres")
    private String pathDoc;
}
