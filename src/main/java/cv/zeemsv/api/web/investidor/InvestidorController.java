package cv.zeemsv.api.web.investidor;

import cv.zeemsv.api.application.investidor.dto.InvestidorRequestDTO;
import cv.zeemsv.api.application.investidor.dto.InvestidorResponseDTO;
import cv.zeemsv.api.application.investidor.dto.InvestidorUserResponseDTO;
import cv.zeemsv.api.application.investidor.service.InvestidorService;
import cv.zeemsv.api.interfaces.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/investidors")
@RequiredArgsConstructor
public class InvestidorController {
    private final InvestidorService service;

    @PostMapping
    public ResponseEntity<ApiResponse<InvestidorResponseDTO>> create(@Valid @RequestBody InvestidorRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Registo criado com sucesso", service.create(dto)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<InvestidorResponseDTO>> update(@PathVariable Integer id, @Valid @RequestBody InvestidorRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok("Registo atualizado com sucesso", service.update(id, dto)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InvestidorResponseDTO>> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok("Registo encontrado", service.findById(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<InvestidorResponseDTO>>> findAll() {
        return ResponseEntity.ok(ApiResponse.ok("Registos encontrados", service.findAll()));
    }

    @GetMapping("/user/email/{email}")
    public ResponseEntity<ApiResponse<List<InvestidorUserResponseDTO>>> findByUserEmail(@PathVariable String email) {
        return ResponseEntity.ok(ApiResponse.ok("Investidores associados ao utilizador encontrados", service.findByUserEmail(email)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Registo eliminado com sucesso", null));
    }
}
