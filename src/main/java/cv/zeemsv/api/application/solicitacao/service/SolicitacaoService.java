package cv.zeemsv.api.application.solicitacao.service;

import cv.zeemsv.api.application.solicitacao.dto.SolicitacaoDocumentosRequisitosResponseDTO;
import cv.zeemsv.api.application.solicitacao.dto.SolicitacaoRequestDTO;
import cv.zeemsv.api.application.solicitacao.dto.SolicitacaoResponseDTO;
import cv.zeemsv.api.application.solicitacao.dto.SubmeterSolicitacaoRequestDTO;
import cv.zeemsv.api.application.solicitacao.dto.ReciboPedidoDadosResponseDTO;
import java.math.BigDecimal;
import java.util.List;

public interface SolicitacaoService {
    SolicitacaoResponseDTO create(SolicitacaoRequestDTO dto);
    SolicitacaoResponseDTO submeter(SubmeterSolicitacaoRequestDTO dto, String authorization);
    SolicitacaoResponseDTO update(Integer id, SolicitacaoRequestDTO dto);
    SolicitacaoResponseDTO findById(Integer id);
    List<SolicitacaoResponseDTO> findAll();
    List<SolicitacaoResponseDTO> findByInvestidorId(Integer idInvestidor);
    ReciboPedidoDadosResponseDTO findReciboDados(Integer idSolicitacao);
    ReciboPedidoDadosResponseDTO findReciboDadosByProcesso(BigDecimal nrProcesso);
    SolicitacaoDocumentosRequisitosResponseDTO findDocumentosByTipoSolicitacaoId(Integer idTpSolicitacao);
    void delete(Integer id);
}
