package cv.zeemsv.api.application.investidor.service;

import cv.zeemsv.api.application.investidor.dto.SocioRepresentanteRequestDTO;
import cv.zeemsv.api.application.investidor.dto.RepresentanteInvestidorResponseDTO;
import cv.zeemsv.api.application.investidor.dto.SocioRepresentanteResponseDTO;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface SocioRepresentanteService {
    SocioRepresentanteResponseDTO create(SocioRepresentanteRequestDTO dto, MultipartFile foto);
    SocioRepresentanteResponseDTO createPendente(SocioRepresentanteRequestDTO dto);
    SocioRepresentanteResponseDTO findById(Integer idSocioRepres);
    List<RepresentanteInvestidorResponseDTO> findSociosByInvestidorId(Integer idInvestidor);
}
