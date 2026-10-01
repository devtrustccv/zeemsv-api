package cv.zeemsv.api.web.cobranca;

import cv.zeemsv.api.application.cobranca.dto.CobrancaPagamentoResponseDTO;
import cv.zeemsv.api.application.cobranca.dto.CriarPagamentoRequestDTO;
import cv.zeemsv.api.application.cobranca.dto.PagamentoIntencaoStatusResponseDTO;
import cv.zeemsv.api.application.cobranca.dto.RealizarPagamentoRequestDTO;
import cv.zeemsv.api.application.cobranca.dto.RealizarPagamentoResponseDTO;
import cv.zeemsv.api.application.cobranca.service.CobrancaService;
import cv.zeemsv.api.application.paymentgateway.dto.PaymentGatewayPaymentValidationRequestDTO;
import cv.zeemsv.api.interfaces.dto.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    public ResponseEntity<ApiResponse<List<CobrancaPagamentoResponseDTO>>> confirmarPagamento(
        @Valid @RequestBody PaymentGatewayPaymentValidationRequestDTO dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok("Callback de pagamento processado com sucesso", cobrancaService.confirmarPagamento(dto)));
    }

    @PostMapping("/manual")
    public ResponseEntity<ApiResponse<CobrancaPagamentoResponseDTO>> criarPagamentoManual(
        @Valid @RequestBody CriarPagamentoRequestDTO dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok("Pagamento criado com sucesso", cobrancaService.criarPagamento(dto)));
    }

    @PostMapping("/realizar-pagamento")
    public ResponseEntity<ApiResponse<RealizarPagamentoResponseDTO>> realizarPagamento(
        @Valid @RequestBody RealizarPagamentoRequestDTO dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok("Intencao de pagamento criada com sucesso", cobrancaService.realizarPagamento(dto)));
    }

    @GetMapping("/intencoes/{intentionId}/status")
    public ResponseEntity<ApiResponse<PagamentoIntencaoStatusResponseDTO>> consultarEstadoPagamento(
        @PathVariable String intentionId
    ) {
        return ResponseEntity.ok(
            ApiResponse.ok("Estado da intencao de pagamento consultado com sucesso", cobrancaService.consultarEstadoPagamento(intentionId))
        );
    }
}
