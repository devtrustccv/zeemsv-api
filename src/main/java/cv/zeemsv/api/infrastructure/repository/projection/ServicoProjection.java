package cv.zeemsv.api.infrastructure.repository.projection;

import java.math.BigDecimal;

public interface ServicoProjection {
    Integer getId();
    String getNome();
    String getDmTipoSolicitacao();
    String getDescricao();
    String getMsgPedido();
    BigDecimal getPrazoDia();
    String getFlagObrigatorio();
    String getCodigo();
    String getDmEstado();
    Integer getIdEntExterna();
    String getPossuiTaxa();
    Boolean getPossuiOnboarding();
    String getEntidadeDenominacao();
    String getEntidadeSigla();
    String getEntidadeDmTipoEnt();
}
