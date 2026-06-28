package cv.zeemsv.api.web.investidor;

import cv.zeemsv.api.application.investidor.dto.PedidoAcessoInvestidorRequestDTO;
import cv.zeemsv.api.application.investidor.dto.PedidoAcessoInvestidorResponseDTO;
import cv.zeemsv.api.application.investidor.service.PedidoAcessoInvestidorService;
import cv.zeemsv.api.interfaces.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/pedidos-acesso-investidor")
@RequiredArgsConstructor
public class PedidoAcessoInvestidorController {
    private final PedidoAcessoInvestidorService service;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<PedidoAcessoInvestidorResponseDTO>> create(
        @Valid @RequestPart("pedido") PedidoAcessoInvestidorRequestDTO dto,
        @RequestPart("ficheiro_compravativo") MultipartFile ficheiroCompravativo
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok("Pedido de acesso ao investidor criado com sucesso", service.create(dto, ficheiroCompravativo)));
    }
}
