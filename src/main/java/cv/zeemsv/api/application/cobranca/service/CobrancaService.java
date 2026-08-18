package cv.zeemsv.api.application.cobranca.service;

import cv.zeemsv.api.application.cobranca.dto.CobrancaInvestidorResponseDTO;
import java.util.List;

public interface CobrancaService {
    List<CobrancaInvestidorResponseDTO> findByInvestidorId(Integer idInvestidor);
}
