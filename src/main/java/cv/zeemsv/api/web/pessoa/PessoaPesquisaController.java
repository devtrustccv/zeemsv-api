package cv.zeemsv.api.web.pessoa;

import cv.zeemsv.api.application.pessoa.dto.PessoaPesquisaResponseDTO;
import cv.zeemsv.api.application.pessoa.service.PessoaPesquisaService;
import cv.zeemsv.api.interfaces.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pessoas")
@RequiredArgsConstructor
public class PessoaPesquisaController {
    private final PessoaPesquisaService service;

    @GetMapping("/pesquisa-cni")
    public ResponseEntity<ApiResponse<List<PessoaPesquisaResponseDTO>>> pesquisar(
        @RequestParam(required = false) String nomeCompleto,
        @RequestParam(required = false) String nrDocumento
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Pesquisa realizada com sucesso", service.pesquisar(nomeCompleto, nrDocumento)));
    }
}
