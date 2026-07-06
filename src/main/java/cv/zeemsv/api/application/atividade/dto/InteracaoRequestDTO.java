package cv.zeemsv.api.application.atividade.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class InteracaoRequestDTO {
    @JsonProperty("id_investidor")
    @Schema(name = "id_investidor", nullable = true, example = "1")
    private Integer idInvestidor;

    @JsonProperty("id_user")
    @Schema(name = "id_user", nullable = true, example = "10")
    private Integer idUser;

    @Schema(nullable = true, example = "Evandro Pinto")
    @Size(max = 255, message = "O campo nome deve ter no maximo 255 caracteres")
    private String nome;

    @Schema(example = "ivatb05170@gmail.com")
    @NotBlank(message = "O campo email e obrigatorio")
    @Email(message = "O campo email deve ser valido")
    @Size(max = 255, message = "O campo email deve ter no maximo 255 caracteres")
    private String email;

    @Schema(nullable = true, example = "+238 9912345")
    @Size(max = 50, message = "O campo telefone deve ter no maximo 50 caracteres")
    private String telefone;

    @JsonProperty("tipo_interacao")
    @Schema(name = "tipo_interacao", example = "ELOGIO")
    @NotBlank(message = "O campo tipo_interacao e obrigatorio")
    @Size(max = 255, message = "O campo tipo_interacao deve ter no maximo 255 caracteres")
    private String tipoInteracao;

    @JsonProperty("assunto_interacao")
    @Schema(name = "assunto_interacao", example = "Teste")
    @NotBlank(message = "O campo assunto_interacao e obrigatorio")
    @Size(max = 500, message = "O campo assunto_interacao deve ter no maximo 500 caracteres")
    private String assuntoInteracao;

    @JsonProperty("mensagem_interacao")
    @Schema(name = "mensagem_interacao", example = "First Teste")
    @NotBlank(message = "O campo mensagem_interacao e obrigatorio")
    @Size(max = 4000, message = "O campo mensagem_interacao deve ter no maximo 4000 caracteres")
    private String mensagemInteracao;
}
