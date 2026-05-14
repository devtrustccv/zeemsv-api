package cv.zeemsv.api.application.investidor.service;

import cv.zeemsv.api.application.investidor.dto.InvestidorRequestDTO;
import cv.zeemsv.api.application.investidor.dto.InvestidorResponseDTO;
import java.util.List;

public interface InvestidorService {
    InvestidorResponseDTO create(InvestidorRequestDTO dto);
    InvestidorResponseDTO update(Integer id, InvestidorRequestDTO dto);
    InvestidorResponseDTO findById(Integer id);
    List<InvestidorResponseDTO> findAll();
    void delete(Integer id);
}
