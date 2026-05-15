package cv.zeemsv.api.application.solicitacao.service;

import cv.zeemsv.api.application.solicitacao.dto.SolicitacaoRequestDTO;
import cv.zeemsv.api.application.solicitacao.dto.SolicitacaoResponseDTO;
import java.util.List;

public interface SolicitacaoService {
    SolicitacaoResponseDTO create(SolicitacaoRequestDTO dto);
    SolicitacaoResponseDTO update(Integer id, SolicitacaoRequestDTO dto);
    SolicitacaoResponseDTO findById(Integer id);
    List<SolicitacaoResponseDTO> findAll();
    List<SolicitacaoResponseDTO> findByInvestidorId(Integer idInvestidor);
    void delete(Integer id);
}
