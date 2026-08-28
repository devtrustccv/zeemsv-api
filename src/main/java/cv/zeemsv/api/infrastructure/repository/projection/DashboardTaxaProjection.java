package cv.zeemsv.api.infrastructure.repository.projection;

import java.math.BigDecimal;

public interface DashboardTaxaProjection {
    String getDescricao();
    BigDecimal getValor();
}
