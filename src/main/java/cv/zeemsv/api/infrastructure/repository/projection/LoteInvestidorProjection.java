package cv.zeemsv.api.infrastructure.repository.projection;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface LoteInvestidorProjection {
    Integer getIdLote();
    String getRefLote();
    String getNip();
    String getDmSituacaoCd();
    String getEstado();
    Integer getIdZona();
    String getZona();
    BigDecimal getArea();
    BigDecimal getAreaInicial();
    Integer getIdInvestidor();
    Integer getIdAssociacao();
    Integer getIdProjeto();
    String getOrigemAssociacao();
    String getDmEstadoAssociacao();
    LocalDate getDataAssociacao();
    String getUtilizadorAssociacao();
    String getDmEnquadramento();
    String getProjetoDenominacao();
    String getProjetoDmRegime();
    String getProjetoDmProdutoServico();
    String getProjetoDmEstadoProc();
    String getProjetoDmSituacao();
    String getProjetoDmEstadoProj();
    LocalDate getProjetoDateCreate();
}
