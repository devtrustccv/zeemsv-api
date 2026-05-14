package cv.zeemsv.api.web.projeto;

import cv.zeemsv.api.application.projeto.dto.ProjetoRequestDTO;
import cv.zeemsv.api.application.projeto.dto.ProjetoResponseDTO;
import cv.zeemsv.api.application.projeto.service.ProjetoService;
import cv.zeemsv.api.interfaces.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/projetos")
@RequiredArgsConstructor
public class ProjetoController {
    private final ProjetoService service;

    @PostMapping
    public ResponseEntity<ApiResponse<ProjetoResponseDTO>> create(@Valid @RequestBody ProjetoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Registo criado com sucesso", service.create(dto)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjetoResponseDTO>> update(@PathVariable Integer id, @Valid @RequestBody ProjetoRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok("Registo atualizado com sucesso", service.update(id, dto)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjetoResponseDTO>> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok("Registo encontrado", service.findById(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProjetoResponseDTO>>> findAll() {
        return ResponseEntity.ok(ApiResponse.ok("Registos encontrados", service.findAll()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Registo eliminado com sucesso", null));
    }
}
