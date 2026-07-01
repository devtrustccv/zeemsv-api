package cv.zeemsv.api.infrastructure.repository.projection;

public interface SolicitacaoDocumentoConfiguradoProjection {
    Integer getIdTpSolicTpDoc();
    Integer getIdTpDoc();
    String getTpDocNome();
    String getTpDocCodigo();
    String getRequisito();
    String getFlagObrigatorio();
    String getPedResp();
}
