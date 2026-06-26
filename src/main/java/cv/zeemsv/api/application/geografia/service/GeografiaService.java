package cv.zeemsv.api.application.geografia.service;

import cv.zeemsv.api.application.geografia.dto.NacionalidadeResponseDTO;
import java.util.List;
import java.util.Map;

public interface GeografiaService {
    Map<String, String> getIndicativos();
    List<NacionalidadeResponseDTO> getNacionalidades();
    Map<String, String> getConcelhosCaboVerde();
}
