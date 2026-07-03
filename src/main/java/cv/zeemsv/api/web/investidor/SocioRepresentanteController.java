package cv.zeemsv.api.web.investidor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cv.zeemsv.api.application.investidor.dto.SocioRepresentanteRequestDTO;
import cv.zeemsv.api.application.investidor.dto.SocioRepresentanteResponseDTO;
import cv.zeemsv.api.application.investidor.dto.SocioRepresentanteUpdateRequestDTO;
import cv.zeemsv.api.application.investidor.service.SocioRepresentanteService;
import cv.zeemsv.api.exceptions.BusinessException;
import cv.zeemsv.api.interfaces.dto.ApiResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/socio-representantes")
@RequiredArgsConstructor
public class SocioRepresentanteController {
    private final SocioRepresentanteService service;
    private final ObjectMapper objectMapper;
    private final Validator validator;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<SocioRepresentanteResponseDTO>> create(
        @RequestPart("socio_representante") String socioRepresentante,
        @RequestPart(value = "foto", required = false) MultipartFile foto
    ) {
        SocioRepresentanteRequestDTO dto = parseSocioRepresentante(socioRepresentante);
        validateSocioRepresentante(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok("Socio/representante criado com sucesso", service.create(dto, foto)));
    }

    @GetMapping("/{idSocioRepres}")
    public ResponseEntity<ApiResponse<SocioRepresentanteResponseDTO>> findById(
        @PathVariable Integer idSocioRepres
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Socio/representante encontrado", service.findById(idSocioRepres)));
    }

    @PutMapping(value = "/{idSocioRepres}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<SocioRepresentanteResponseDTO>> update(
        @PathVariable Integer idSocioRepres,
        @RequestPart(value = "socio_representante", required = false) String socioRepresentante,
        @RequestPart(value = "foto", required = false) MultipartFile foto
    ) {
        SocioRepresentanteUpdateRequestDTO dto = parseSocioRepresentanteUpdate(socioRepresentante);
        validateSocioRepresentanteUpdate(dto);
        return ResponseEntity.ok(ApiResponse.ok("Socio/representante atualizado com sucesso", service.update(idSocioRepres, dto, foto)));
    }

    private SocioRepresentanteRequestDTO parseSocioRepresentante(String socioRepresentante) {
        try {
            return objectMapper.readValue(socioRepresentante, SocioRepresentanteRequestDTO.class);
        } catch (JsonProcessingException ex) {
            throw new BusinessException("O campo socio_representante deve conter JSON valido.", ex);
        }
    }

    private void validateSocioRepresentante(SocioRepresentanteRequestDTO dto) {
        Set<ConstraintViolation<SocioRepresentanteRequestDTO>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    private SocioRepresentanteUpdateRequestDTO parseSocioRepresentanteUpdate(String socioRepresentante) {
        if (socioRepresentante == null || socioRepresentante.trim().isEmpty()) {
            return new SocioRepresentanteUpdateRequestDTO();
        }
        try {
            return objectMapper.readValue(socioRepresentante, SocioRepresentanteUpdateRequestDTO.class);
        } catch (JsonProcessingException ex) {
            throw new BusinessException("O campo socio_representante deve conter JSON valido.", ex);
        }
    }

    private void validateSocioRepresentanteUpdate(SocioRepresentanteUpdateRequestDTO dto) {
        Set<ConstraintViolation<SocioRepresentanteUpdateRequestDTO>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
