package cv.zeemsv.api.application.investidor.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class DashboardCountDTO {
    private String codigo;
    private String descricao;
    private Long total;
}
