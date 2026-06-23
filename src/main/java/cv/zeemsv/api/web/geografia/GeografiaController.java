package cv.zeemsv.api.web.geografia;

import cv.zeemsv.api.application.geografia.service.GeografiaService;
import cv.zeemsv.api.interfaces.dto.ApiResponse;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/geografias")
@RequiredArgsConstructor
public class GeografiaController {
    private final GeografiaService service;

    @GetMapping("/indicativos")
    public ResponseEntity<ApiResponse<Map<String, String>>> getIndicativos() {
        return ResponseEntity.ok(ApiResponse.ok("Indicativos encontrados", service.getIndicativos()));
    }

    @GetMapping("/nacionalidades")
    public ResponseEntity<ApiResponse<Map<String, String>>> getNacionalidades() {
        return ResponseEntity.ok(ApiResponse.ok("Nacionalidades encontradas", service.getNacionalidades()));
    }

    @GetMapping("/concelhos/cabo-verde")
    public ResponseEntity<ApiResponse<Map<String, String>>> getConcelhosCaboVerde() {
        return ResponseEntity.ok(ApiResponse.ok("Concelhos encontrados", service.getConcelhosCaboVerde()));
    }
}
