package cv.zeemsv.api.application.investidor.service;

import cv.zeemsv.api.application.investidor.dto.RepresentanteInvestidorResponseDTO;

import java.util.List;

public interface RepresentanteInvestidorService {
    List<RepresentanteInvestidorResponseDTO> findByInvestidorId(Integer idInvestidor);
}
