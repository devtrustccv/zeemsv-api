package cv.zeemsv.api.application.zona.service;

import cv.zeemsv.api.application.zona.dto.ZonaCoordenadasResponseDTO;
import java.util.List;

public interface ZonaService {
    List<ZonaCoordenadasResponseDTO> findZonasComCoordenadasELotes();
}
