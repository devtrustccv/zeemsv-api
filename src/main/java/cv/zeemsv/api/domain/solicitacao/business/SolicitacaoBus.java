package cv.zeemsv.api.domain.solicitacao.business;

import cv.zeemsv.api.domain.solicitacao.model.Solicitacao;
import java.util.List;

public interface SolicitacaoBus {
    Solicitacao create(Solicitacao model);
    Solicitacao update(Integer id, Solicitacao model);
    Solicitacao findById(Integer id);
    List<Solicitacao> findAll();
    void delete(Integer id);
}
