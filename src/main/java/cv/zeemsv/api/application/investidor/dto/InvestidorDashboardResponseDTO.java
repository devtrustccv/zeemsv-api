package cv.zeemsv.api.application.investidor.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class InvestidorDashboardResponseDTO {
    private Integer idInvestidor;
    private Long totalLote;
    private Long totalLoteReservado;
    private Long totalProjeto;
    private List<DashboardCountDTO> projetoPorSituacao;
    private Long totalProcesso;
    private List<DashboardCountDTO> processoPorEstado;
    private List<DashboardCountDTO> processoPorEtapa;
}
