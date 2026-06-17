package cv.zeemsv.api.application.solicitacao.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SolicitacaoRequestDTO {
    @NotBlank(message = "O campo nome é obrigatório")
    private String nome;
    private String descricao;
    private String estado;
    private Boolean flagCorrecao;
    private LocalDate dataEnvioCorrecao;
    private LocalDate dataFimPrevistaCorrecao;
    private LocalDate dataCorrecao;
    private String userCorecao;
}
