package cv.zeemsv.api.web.investidor;

import cv.zeemsv.api.application.investidor.dto.SocioRepresentanteRequestDTO;
import cv.zeemsv.api.application.investidor.dto.SocioRepresentanteResponseDTO;
import cv.zeemsv.api.application.investidor.service.SocioRepresentanteService;
import cv.zeemsv.api.interfaces.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/socio-representantes")
@RequiredArgsConstructor
public class SocioRepresentanteController {
    private final SocioRepresentanteService service;

    @PostMapping
    public ResponseEntity<ApiResponse<SocioRepresentanteResponseDTO>> create(
        @Valid @RequestBody SocioRepresentanteRequestDTO dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok("Socio/representante criado com sucesso", service.create(dto)));
    }
}
