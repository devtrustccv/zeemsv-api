package cv.zeemsv.api.application.atividade.service;

import cv.zeemsv.api.application.atividade.dto.AtividadeResponseDTO;
import cv.zeemsv.api.application.atividade.dto.InteracaoRequestDTO;
import cv.zeemsv.api.application.atividade.dto.InteracaoResponseDTO;
import cv.zeemsv.api.application.atividade.dto.NotificacaoInvestidorResponseDTO;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface AtividadeService {
    AtividadeResponseDTO createInteracao(InteracaoRequestDTO dto);
    AtividadeResponseDTO createInteracao(InteracaoRequestDTO dto, MultipartFile anexo);
    List<InteracaoResponseDTO> findInteracoes(Integer idUser, Integer idInvestidor, String email);
    List<NotificacaoInvestidorResponseDTO> findNotificacoesByInvestidorId(Integer idInvestidor);
    List<NotificacaoInvestidorResponseDTO> findNotificacoesByUserId(Integer idUser);
    List<AtividadeResponseDTO> findAgendadasByInvestidorId(Integer idInvestidor);
}
