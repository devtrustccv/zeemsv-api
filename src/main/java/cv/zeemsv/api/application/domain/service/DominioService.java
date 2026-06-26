package cv.zeemsv.api.application.domain.service;

import cv.zeemsv.api.application.domain.dto.DominioResponseDTO;
import cv.zeemsv.api.application.domain.dto.DominioValorResponseDTO;
import cv.zeemsv.api.application.domain.dto.DominioValoresResponseDTO;

import java.util.List;

public interface DominioService {
    List<DominioResponseDTO> findAll();
    List<DominioValorResponseDTO> findValoresByDominio(String dominio);
    List<DominioValoresResponseDTO> findValoresByDominios(List<String> dominios);
}
