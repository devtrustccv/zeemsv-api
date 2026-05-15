package cv.zeemsv.api.application.ordem.service;

import cv.zeemsv.api.application.ordem.dto.OrdemResponseDTO;

import java.util.List;

public interface OrdemService {
    List<OrdemResponseDTO> findAll();
}
