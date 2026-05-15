package cv.zeemsv.api.application.projeto.service;

import cv.zeemsv.api.application.projeto.dto.ProjetoRequestDTO;
import cv.zeemsv.api.application.projeto.dto.ProjetoResponseDTO;
import java.util.List;

public interface ProjetoService {
    ProjetoResponseDTO create(ProjetoRequestDTO dto);
    ProjetoResponseDTO update(Integer id, ProjetoRequestDTO dto);
    ProjetoResponseDTO findById(Integer id);
    List<ProjetoResponseDTO> findAll();
    List<ProjetoResponseDTO> findByInvestidorId(Integer idInvestidor);
    void delete(Integer id);
}
