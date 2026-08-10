package cv.zeemsv.api.application.investidor.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DashboardAlertasDTO {
    private String titulo = "Alertas";
    private Long critico;
    private Long atencao;
    private Long informativos;
}
