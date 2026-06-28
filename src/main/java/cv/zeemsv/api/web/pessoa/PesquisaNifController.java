package cv.zeemsv.api.web.pessoa;

import cv.zeemsv.api.application.pessoa.dto.PesquisaNifResponseDTO;
import cv.zeemsv.api.application.pessoa.service.PesquisaNifService;
import cv.zeemsv.api.interfaces.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/pessoas")
@RequiredArgsConstructor
public class PesquisaNifController {
    private final PesquisaNifService service;

    @GetMapping("/pesquisa-nif")
    public ResponseEntity<ApiResponse<PesquisaNifResponseDTO>> pesquisar(@RequestParam String nif) {
        return ResponseEntity.ok(ApiResponse.ok("Pesquisa realizada com sucesso", service.pesquisar(nif).orElse(null)));
    }

}
