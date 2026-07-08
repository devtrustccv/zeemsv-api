package cv.zeemsv.api.infrastructure.repository.projection;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public interface NotificacaoInvestidorProjection {
    Integer getIdRelacaoNotificacao();
    String getTpRelacao();
    BigDecimal getIdRelacao();
    Integer getIdNotificacao();
    Integer getIdPai();
    BigDecimal getIdAplicacao();
    BigDecimal getIdOrganica();
    BigDecimal getUserRegisto();
    LocalDate getDataRegisto();
    String getAssunto();
    LocalDateTime getDataEnvio();
    String getMensagem();
    String getMensagemConfirmacao();
    String getEmail();
    String getTelemovel();
    String getEstado();
    String getFlagAutomatico();
    String getFlagSucesso();
    String getFlagLeitura();
    BigDecimal getUserLeitura();
    LocalDateTime getDataLeitura();
    BigDecimal getNumeroReenvios();
    String getTipo();
    Integer getNotificacaoIdRelacao();
    String getDe();
    String getEmailsEnviados();
    Boolean getConfirmRecebimento();
    Long getTotalAnexos();

    Integer getAtividadeId();
    String getAtividadeDmTipoAtividade();
    String getAtividadeTitulo();
    String getAtividadeResumo();
    String getAtividadeDmEstadoAtividade();
    LocalDate getAtividadeDataCreate();

    Integer getSolicitacaoId();
    Integer getSolicitacaoIdTpSolicitacao();
    String getSolicitacaoTipoNome();
    BigDecimal getSolicitacaoIdProcesso();
    String getSolicitacaoDmEstadoProc();
    LocalDate getSolicitacaoDataSolic();

    Integer getProjetoId();
    String getProjetoDenominacao();
    String getProjetoDmRegime();
    String getProjetoDmProdutoServico();
    String getProjetoDmEstadoProc();
    String getProjetoDmSituacao();

    Integer getLoteId();
    String getLoteRefLote();
    String getLoteNip();
    String getLoteDmSituacaoCd();
    String getLoteZona();
}
