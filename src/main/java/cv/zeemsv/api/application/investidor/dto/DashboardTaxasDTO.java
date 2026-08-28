package cv.zeemsv.api.application.investidor.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DashboardTaxasDTO {
    private String titulo = "Taxas em atraso";
    private String totalEmAtraso;
    private List<DashboardTaxaItemDTO> taxas;
}
