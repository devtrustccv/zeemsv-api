package cv.zeemsv.api.application.atividade.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class AtividadeBadgesResponseDTO {
    private Long notificacoes;
    private Long agendamentos;
}
