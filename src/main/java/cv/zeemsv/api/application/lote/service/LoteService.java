package cv.zeemsv.api.application.lote.service;

import cv.zeemsv.api.application.lote.dto.LoteRequestDTO;
import cv.zeemsv.api.application.lote.dto.LoteInvestidorResponseDTO;
import cv.zeemsv.api.application.lote.dto.LoteResponseDTO;
import java.util.List;

public interface LoteService {
    LoteResponseDTO create(LoteRequestDTO dto);
    LoteResponseDTO update(Integer id, LoteRequestDTO dto);
    LoteResponseDTO findById(Integer id);
    List<LoteResponseDTO> findAll();
    List<LoteInvestidorResponseDTO> findByInvestidorId(Integer idInvestidor);
    void delete(Integer id);
}
