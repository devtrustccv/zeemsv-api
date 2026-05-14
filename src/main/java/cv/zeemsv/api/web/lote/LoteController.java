package cv.zeemsv.api.web.lote;

import cv.zeemsv.api.application.lote.dto.LoteRequestDTO;
import cv.zeemsv.api.application.lote.dto.LoteResponseDTO;
import cv.zeemsv.api.application.lote.service.LoteService;
import cv.zeemsv.api.interfaces.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/lotes")
@RequiredArgsConstructor
public class LoteController {
    private final LoteService service;

    @PostMapping
    public ResponseEntity<ApiResponse<LoteResponseDTO>> create(@Valid @RequestBody LoteRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Registo criado com sucesso", service.create(dto)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<LoteResponseDTO>> update(@PathVariable Integer id, @Valid @RequestBody LoteRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok("Registo atualizado com sucesso", service.update(id, dto)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LoteResponseDTO>> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok("Registo encontrado", service.findById(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<LoteResponseDTO>>> findAll() {
        return ResponseEntity.ok(ApiResponse.ok("Registos encontrados", service.findAll()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Registo eliminado com sucesso", null));
    }
}
