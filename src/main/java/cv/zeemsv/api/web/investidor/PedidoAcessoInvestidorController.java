package cv.zeemsv.api.web.investidor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cv.zeemsv.api.application.investidor.dto.PedidoAcessoInvestidorRequestDTO;
import cv.zeemsv.api.application.investidor.dto.PedidoAcessoInvestidorResponseDTO;
import cv.zeemsv.api.application.investidor.service.PedidoAcessoInvestidorService;
import cv.zeemsv.api.exceptions.BusinessException;
import cv.zeemsv.api.interfaces.dto.ApiResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    private final ObjectMapper objectMapper;
    private final Validator validator;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<PedidoAcessoInvestidorResponseDTO>> create(
        @RequestPart("pedido") String pedido,
        @RequestPart("ficheiro_compravativo") MultipartFile ficheiroCompravativo
    ) {
        PedidoAcessoInvestidorRequestDTO dto = parsePedido(pedido);
        validatePedido(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok("Pedido de acesso ao investidor criado com sucesso", service.create(dto, ficheiroCompravativo)));
    }

    @GetMapping("/user/{idUser}")
    public ResponseEntity<ApiResponse<List<PedidoAcessoInvestidorResponseDTO>>> findByUserId(
        @PathVariable Integer idUser
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Pedidos de acesso do utilizador encontrados", service.findByUserId(idUser)));
    }

    private PedidoAcessoInvestidorRequestDTO parsePedido(String pedido) {
        try {
            return objectMapper.readValue(pedido, PedidoAcessoInvestidorRequestDTO.class);
        } catch (JsonProcessingException ex) {
            throw new BusinessException("O campo pedido deve conter JSON valido.", ex);
        }
    }

    private void validatePedido(PedidoAcessoInvestidorRequestDTO dto) {
        Set<ConstraintViolation<PedidoAcessoInvestidorRequestDTO>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
