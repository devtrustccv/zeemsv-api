package cv.zeemsv.api.web.cobranca;

import cv.zeemsv.api.application.cobranca.dto.CobrancaPagamentoResponseDTO;
import cv.zeemsv.api.application.cobranca.dto.CriarPagamentoRequestDTO;
import cv.zeemsv.api.application.cobranca.dto.PagamentoIntencaoStatusResponseDTO;
import cv.zeemsv.api.application.cobranca.dto.RealizarPagamentoRequestDTO;
import cv.zeemsv.api.application.cobranca.dto.RealizarPagamentoResponseDTO;
import cv.zeemsv.api.application.cobranca.service.CobrancaService;
import cv.zeemsv.api.application.paymentgateway.dto.PaymentGatewayPaymentValidationRequestDTO;
import cv.zeemsv.api.config.PaymentGatewayProperties;
import cv.zeemsv.api.exceptions.BusinessException;
import cv.zeemsv.api.interfaces.dto.ApiResponse;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
    private final PaymentGatewayProperties paymentGatewayProperties;

    @PostMapping
    public ResponseEntity<ApiResponse<List<CobrancaPagamentoResponseDTO>>> confirmarPagamento(
        @Valid @RequestBody PaymentGatewayPaymentValidationRequestDTO dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok("Callback de pagamento processado com sucesso", cobrancaService.confirmarPagamento(dto)));
    }

    @PostMapping(
        value = "/confirmar-pagamentos",
        consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    public ResponseEntity<Void> confirmarPagamentosForm(
        @Valid @ModelAttribute PaymentGatewayPaymentValidationRequestDTO dto
    ) {
        String intentionId = cobrancaService.confirmarPagamentoERetornarIntentionId(dto);
        return ResponseEntity.status(HttpStatus.SEE_OTHER)
            .location(buildConfirmationRedirectUri(intentionId))
            .build();
    }

    private URI buildConfirmationRedirectUri(String intentionId) {
        String redirectUrl = paymentGatewayProperties.getConfirmationRedirectUrl();
        if (!StringUtils.hasText(redirectUrl)) {
            throw new BusinessException("URL de redirecionamento de confirmacao de pagamento nao configurada.");
        }
        return UriComponentsBuilder.fromUriString(redirectUrl)
            .queryParam("intentionId", intentionId)
            .build()
            .encode()
            .toUri();
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
