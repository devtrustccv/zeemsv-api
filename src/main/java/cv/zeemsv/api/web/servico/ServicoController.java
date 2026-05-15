package cv.zeemsv.api.web.servico;

import cv.zeemsv.api.application.servico.dto.ServicoResponseDTO;
import cv.zeemsv.api.application.servico.service.ServicoService;
import cv.zeemsv.api.interfaces.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.util.StringUtils;

import java.util.List;

@RestController
@RequestMapping("/api/v1/servicos")
@RequiredArgsConstructor
public class ServicoController {
    private final ServicoService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ServicoResponseDTO>>> findAll(
        @RequestParam(required = false) String dmTpRepresentante
    ) {
        List<ServicoResponseDTO> servicos = StringUtils.hasText(dmTpRepresentante)
            ? service.findByTipoRepresentante(dmTpRepresentante)
            : service.findAll();
        return ResponseEntity.ok(ApiResponse.ok("Servicos encontrados", servicos));
    }
}
