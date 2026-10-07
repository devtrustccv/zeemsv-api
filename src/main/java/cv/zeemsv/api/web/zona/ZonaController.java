package cv.zeemsv.api.web.zona;

import cv.zeemsv.api.application.zona.dto.ZonaCoordenadasResponseDTO;
import cv.zeemsv.api.application.zona.service.ZonaService;
import cv.zeemsv.api.interfaces.dto.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/zonas")
@RequiredArgsConstructor
public class ZonaController {
    private final ZonaService service;

    @GetMapping("/coordenadas")
    public ResponseEntity<ApiResponse<List<ZonaCoordenadasResponseDTO>>> findZonasComCoordenadasELotes() {
        return ResponseEntity.ok(ApiResponse.ok("Zonas com coordenadas encontradas", service.findZonasComCoordenadasELotes()));
    }
}
