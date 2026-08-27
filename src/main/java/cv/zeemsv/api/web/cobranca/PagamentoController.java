package cv.zeemsv.api.web.cobranca;

import cv.zeemsv.api.application.cobranca.dto.CobrancaPagamentoResponseDTO;
import cv.zeemsv.api.application.cobranca.dto.CriarPagamentoRequestDTO;
import cv.zeemsv.api.application.cobranca.service.CobrancaService;
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
@RequestMapping("/api/v1/pagamentos")
@RequiredArgsConstructor
public class PagamentoController {
    private final CobrancaService cobrancaService;

    @PostMapping
    public ResponseEntity<ApiResponse<CobrancaPagamentoResponseDTO>> criarPagamento(
        @Valid @RequestBody CriarPagamentoRequestDTO dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok("Pagamento criado com sucesso", cobrancaService.criarPagamento(dto)));
    }
}
