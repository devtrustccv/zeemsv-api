package cv.zeemsv.api.application.investidor.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class DashboardAuditoriaDTO {
    private LocalDateTime data;
    private String utilizador;
    private String acaoRealizada;
    private String acao;
    private String tabela;
    private String idTabela;
}
