package cv.zeemsv.api.application.servico.service;

import cv.zeemsv.api.application.servico.dto.ServicoResponseDTO;

import java.util.List;

public interface ServicoService {
    List<ServicoResponseDTO> findAll();
    List<ServicoResponseDTO> findByTipoRepresentante(String dmTpRepresentante);
}
