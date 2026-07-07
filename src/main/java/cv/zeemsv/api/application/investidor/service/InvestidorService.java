package cv.zeemsv.api.application.investidor.service;

import cv.zeemsv.api.application.investidor.dto.InvestidorDashboardResponseDTO;
import cv.zeemsv.api.application.investidor.dto.InvestidorDocumentoResponseDTO;
import cv.zeemsv.api.application.investidor.dto.InvestidorRequestDTO;
import cv.zeemsv.api.application.investidor.dto.InvestidorResponseDTO;
import cv.zeemsv.api.application.investidor.dto.InvestidorUserResponseDTO;
import java.util.List;

public interface InvestidorService {
    InvestidorResponseDTO create(InvestidorRequestDTO dto);
    InvestidorResponseDTO update(Integer id, InvestidorRequestDTO dto);
    InvestidorResponseDTO findById(Integer id);
    List<InvestidorResponseDTO> findAll();
    List<InvestidorUserResponseDTO> findByUserEmail(String email);
    List<InvestidorDocumentoResponseDTO> findDocumentosByInvestidorId(Integer idInvestidor);
    InvestidorDashboardResponseDTO getDashboard(Integer idInvestidor);
    void delete(Integer id);
}
