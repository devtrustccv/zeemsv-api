package cv.zeemsv.api.application.investidor.service;

import cv.zeemsv.api.application.investidor.dto.AssociarRepresentanteRequestDTO;
import cv.zeemsv.api.application.investidor.dto.RepresentanteInvestidorResponseDTO;

public interface AssociarRepresentanteService {
    RepresentanteInvestidorResponseDTO associar(Integer idInvestidor, AssociarRepresentanteRequestDTO dto);
}
