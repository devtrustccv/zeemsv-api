package cv.zeemsv.api.infrastructure.repository.projection;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface SolicitacaoDocProjection {
    Integer getId();
    Integer getIdSolicitacao();
    Integer getIdTpSolicTpDoc();
    Integer getIdTpDoc();
    String getTpDocNome();
    String getTpDocCodigo();
    String getRequisito();
    String getFlagObrigatorio();
    String getPedResp();
    BigDecimal getIdProcesso();
    BigDecimal getIdEtapa();
    LocalDate getDataRegisto();
    String getUserRegisto();
    String getPath();
}
