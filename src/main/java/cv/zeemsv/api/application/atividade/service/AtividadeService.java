package cv.zeemsv.api.application.atividade.service;

import cv.zeemsv.api.application.atividade.dto.AtividadeResponseDTO;
import cv.zeemsv.api.application.atividade.dto.NotificacaoInvestidorResponseDTO;

import java.util.List;

public interface AtividadeService {
    List<NotificacaoInvestidorResponseDTO> findNotificacoesByInvestidorId(Integer idInvestidor);
    List<NotificacaoInvestidorResponseDTO> findNotificacoesByUserId(Integer idUser);
    List<AtividadeResponseDTO> findAgendadasByInvestidorId(Integer idInvestidor);
}
