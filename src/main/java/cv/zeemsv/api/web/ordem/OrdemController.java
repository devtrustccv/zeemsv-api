package cv.zeemsv.api.web.ordem;

import cv.zeemsv.api.application.ordem.dto.OrdemResponseDTO;
import cv.zeemsv.api.application.ordem.service.OrdemService;
import cv.zeemsv.api.interfaces.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ordens")
@RequiredArgsConstructor
public class OrdemController {
    private final OrdemService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrdemResponseDTO>>> findAll() {
        return ResponseEntity.ok(ApiResponse.ok("Ordens encontradas", service.findAll()));
    }
}
