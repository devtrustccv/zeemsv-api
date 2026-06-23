package cv.zeemsv.api.application.investidor.service;

import cv.zeemsv.api.application.investidor.dto.SocioRepresentanteRequestDTO;
import cv.zeemsv.api.application.investidor.dto.SocioRepresentanteResponseDTO;

public interface SocioRepresentanteService {
    SocioRepresentanteResponseDTO create(SocioRepresentanteRequestDTO dto);
}
