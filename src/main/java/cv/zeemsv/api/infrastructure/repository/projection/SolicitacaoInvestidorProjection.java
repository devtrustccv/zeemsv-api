package cv.zeemsv.api.infrastructure.repository.projection;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface SolicitacaoInvestidorProjection {
    Integer getId();
    String getNome();
    String getDescricao();
    String getEstado();
    Integer getIdTpSolicitacao();
    Integer getIdPedido();
    Integer getIdEntidade();
    BigDecimal getIdOrganica();
    BigDecimal getIdProcesso();
    Integer getIdSolicPai();
    Integer getIdPromotor();
    Integer getIdInvestidor();
    Integer getIdProjeto();
    String getExposicao();
    String getDmOrigem();
    String getUserSolic();
    LocalDate getDataSolic();
    LocalDate getDataPrevResposta();
    String getDescSolic();
    String getDmEstadoProc();
    LocalDate getDataResposta();
    BigDecimal getUserResposta();
    String getDescResposta();
    BigDecimal getPrazoDia();
    BigDecimal getPrazoReal();
    String getEtapaAtual();
    Integer getIdPontoFocalResp();

    String getTpSolicitacaoNome();
    String getTpSolicitacaoDescricao();
    String getTpSolicitacaoCodigo();
    String getTpSolicitacaoDmTipo();
    String getTpSolicitacaoDmEstado();

    String getInvestidorDenominacao();
    String getInvestidorNif();
    String getInvestidorEmail();
    BigDecimal getInvestidorTelemovel();
    String getInvestidorPaisOrigem();

    String getPromotorDenominacao();
    String getPromotorNif();
    String getPromotorEmail();
    BigDecimal getPromotorTelemovel();
    String getPromotorPaisOrigem();

    String getProjetoDenominacao();
    String getProjetoDmRegime();
    String getProjetoDmProdutoServico();
    String getProjetoDmEstadoProc();
    String getProjetoDmSituacao();

    String getPedidoDmEstadoPedido();
    String getPedidoEtapaAtual();
    String getPedidoCodEtapaAtual();
    LocalDate getPedidoDtRegisto();
    LocalDate getPedidoDtDespacho();
    LocalDate getPedidoDtFim();
    String getPedidoObsDespacho();
    String getPedidoResultado();
    String getPedidoRequerente();
}
