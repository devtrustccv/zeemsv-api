package cv.zeemsv.api.web.cobranca;

import cv.zeemsv.api.application.cobranca.dto.ComprovativoPagamentoResponseDTO;
import cv.zeemsv.api.application.cobranca.dto.ComprovativoPagamentoRequestDTO;
import jakarta.validation.Valid;
import cv.zeemsv.api.application.cobranca.service.CobrancaService;
import cv.zeemsv.api.interfaces.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/cobrancas")
@RequiredArgsConstructor
public class CobrancaController {
    private final CobrancaService cobrancaService;

    @PostMapping(value = "/{idCobranca}/comprovativo-pagamento", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ComprovativoPagamentoResponseDTO>> receberComprovativoPagamento(
        @PathVariable Integer idCobranca,
        @Valid @RequestPart("dados") ComprovativoPagamentoRequestDTO dto,
        @RequestPart("ficheiro") MultipartFile ficheiro
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Pagamento pendente de validacao criado com sucesso",
            cobrancaService.receberComprovativoPagamento(idCobranca, dto, ficheiro)));
    }
}
