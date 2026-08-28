package cv.zeemsv.api.application.investidor.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DashboardPagamentosDTO {
    private String titulo = "Pagamentos";
    private String totalPagamentos;
    private Long pagamentosPendentes;
}
