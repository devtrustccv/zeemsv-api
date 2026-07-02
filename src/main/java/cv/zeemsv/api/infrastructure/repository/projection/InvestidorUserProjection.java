package cv.zeemsv.api.infrastructure.repository.projection;

import java.math.BigDecimal;

public interface InvestidorUserProjection {
    Integer getId();
    String getDenominacao();
    String getNif();
    String getEmail();
    BigDecimal getTelemovel();
    String getDmEstado();
    String getDmTipoInvestidor();
    String getPaisOrigem();
    String getEndereco();
    String getDmTpRepresentante();
    String getDmPrincipal();
}
