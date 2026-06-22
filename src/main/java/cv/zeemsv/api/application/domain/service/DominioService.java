package cv.zeemsv.api.application.domain.service;

import cv.zeemsv.api.application.domain.dto.DominioResponseDTO;
import cv.zeemsv.api.application.domain.dto.DominioValorResponseDTO;

import java.util.List;

public interface DominioService {
    List<DominioResponseDTO> findAll();
    List<DominioValorResponseDTO> findValoresByDominio(String dominio);
}
