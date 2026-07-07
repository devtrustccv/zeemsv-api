package cv.zeemsv.api.infrastructure.repository.projection;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface InvestidorDocumentoProjection {
    Integer getId();
    String getTipoRelacao();
    BigDecimal getIdRelacao();
    String getNomeDocumento();
    String getObjetoDescricao();
    String getIdDoc();
    Integer getIdTpDoc();
    String getEstado();
    LocalDateTime getDateCreate();
    String getUserCreate();
    String getPath();
    BigDecimal getDocSize();
    String getMimetype();
    String getDescricao();
}
