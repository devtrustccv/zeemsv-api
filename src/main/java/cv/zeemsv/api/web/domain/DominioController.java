package cv.zeemsv.api.web.domain;

import cv.zeemsv.api.application.domain.dto.DominioResponseDTO;
import cv.zeemsv.api.application.domain.dto.DominioValorResponseDTO;
import cv.zeemsv.api.application.domain.service.DominioService;
import cv.zeemsv.api.interfaces.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dominios")
@RequiredArgsConstructor
public class DominioController {
    private final DominioService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<DominioResponseDTO>>> findAll() {
        return ResponseEntity.ok(ApiResponse.ok("Dominios encontrados", service.findAll()));
    }

    @GetMapping("/{dominio}/valores")
    public ResponseEntity<ApiResponse<List<DominioValorResponseDTO>>> findValoresByDominio(@PathVariable String dominio) {
        return ResponseEntity.ok(ApiResponse.ok("Valores do dominio encontrados", service.findValoresByDominio(dominio)));
    }
}
