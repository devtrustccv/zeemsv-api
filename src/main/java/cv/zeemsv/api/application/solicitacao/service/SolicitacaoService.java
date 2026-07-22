package cv.zeemsv.api.application.solicitacao.service;

import cv.zeemsv.api.application.solicitacao.dto.SolicitacaoDocumentosRequisitosResponseDTO;
import cv.zeemsv.api.application.solicitacao.dto.SolicitacaoRequestDTO;
import cv.zeemsv.api.application.solicitacao.dto.SolicitacaoResponseDTO;
import cv.zeemsv.api.application.solicitacao.dto.SubmeterSolicitacaoRequestDTO;
import java.util.List;

public interface SolicitacaoService {
    SolicitacaoResponseDTO create(SolicitacaoRequestDTO dto);
    SolicitacaoResponseDTO submeter(SubmeterSolicitacaoRequestDTO dto, String authorization);
    SolicitacaoResponseDTO update(Integer id, SolicitacaoRequestDTO dto);
    SolicitacaoResponseDTO findById(Integer id);
    List<SolicitacaoResponseDTO> findAll();
    List<SolicitacaoResponseDTO> findByInvestidorId(Integer idInvestidor);
    SolicitacaoDocumentosRequisitosResponseDTO findDocumentosByTipoSolicitacaoId(Integer idTpSolicitacao);
    void delete(Integer id);
}
