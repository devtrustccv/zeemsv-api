package cv.zeemsv.api.infrastructure.repository.projection;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface ProjetoInvestidorProjection {
    Integer getId();
    String getEstado();
    String getDenominacao();
    String getDmEnquadrameno();
    String getDmRegime();
    String getDmProdutoServico();
    String getDmEstadoInstall();
    String getDmEstadoProc();
    LocalDate getDateCreate();
    BigDecimal getUserCreate();
    LocalDate getDateUpdate();
    BigDecimal getUserUpdate();
    Boolean getDmDocFalta();
    Integer getIdInvestidorCae();
    String getDmSituacao();
    Integer getIdInvestidor();
    String getDmEstadoProj();
    LocalDate getDataDesistencia();
    BigDecimal getUserDesistencia();
    String getMotivo();
    String getAtividadePrincipal();
    String getAtividadePrincipalSetor();
}
