package cv.zeemsv.api.application.investidor.service;

import cv.zeemsv.api.application.investidor.dto.SocioRepresentanteRequestDTO;
import cv.zeemsv.api.application.investidor.dto.RepresentanteInvestidorResponseDTO;
import cv.zeemsv.api.application.investidor.dto.SocioRepresentanteResponseDTO;
import java.util.List;

public interface SocioRepresentanteService {
    SocioRepresentanteResponseDTO create(SocioRepresentanteRequestDTO dto);
    SocioRepresentanteResponseDTO createPendente(SocioRepresentanteRequestDTO dto);
    List<RepresentanteInvestidorResponseDTO> findSociosByInvestidorId(Integer idInvestidor);
}
