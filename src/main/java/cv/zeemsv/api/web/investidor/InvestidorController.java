package cv.zeemsv.api.web.investidor;

import cv.zeemsv.api.application.investidor.dto.AssociarRepresentanteRequestDTO;
import cv.zeemsv.api.application.investidor.dto.InvestidorDocumentoResponseDTO;
import cv.zeemsv.api.application.investidor.dto.InvestidorRequestDTO;
import cv.zeemsv.api.application.investidor.dto.InvestidorResponseDTO;
import cv.zeemsv.api.application.investidor.dto.InvestidorUserResponseDTO;
import cv.zeemsv.api.application.investidor.dto.RepresentanteInvestidorResponseDTO;
import cv.zeemsv.api.application.investidor.service.InvestidorService;
import cv.zeemsv.api.application.investidor.service.AssociarRepresentanteService;
import cv.zeemsv.api.application.investidor.service.RepresentanteInvestidorService;
import cv.zeemsv.api.application.investidor.service.SocioRepresentanteService;
import cv.zeemsv.api.application.pessoa.dto.PesquisaNifResponseDTO;
import cv.zeemsv.api.application.pessoa.service.PesquisaNifService;
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
    private final RepresentanteInvestidorService representanteInvestidorService;
    private final AssociarRepresentanteService associarRepresentanteService;
    private final SocioRepresentanteService socioRepresentanteService;
    private final PesquisaNifService pesquisaNifService;

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

    @GetMapping("/{idInvestidor}/documentos")
    public ResponseEntity<ApiResponse<List<InvestidorDocumentoResponseDTO>>> findDocumentosByInvestidorId(
        @PathVariable Integer idInvestidor
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Documentos do investidor encontrados", service.findDocumentosByInvestidorId(idInvestidor)));
    }

    @GetMapping("/user/email/{email}")
    public ResponseEntity<ApiResponse<List<InvestidorUserResponseDTO>>> findByUserEmail(@PathVariable String email) {
        return ResponseEntity.ok(ApiResponse.ok("Investidores associados ao utilizador encontrados", service.findByUserEmail(email)));
    }

    @GetMapping("/pesquisa-nif-local")
    public ResponseEntity<ApiResponse<PesquisaNifResponseDTO>> pesquisarLocalPorNif(@RequestParam String nif) {
        return ResponseEntity.ok(ApiResponse.ok("Pesquisa local realizada com sucesso", pesquisaNifService.pesquisarLocal(nif).orElse(null)));
    }

    @GetMapping("/{idInvestidor}/representantes")
    public ResponseEntity<ApiResponse<List<RepresentanteInvestidorResponseDTO>>> findRepresentantesByInvestidor(
        @PathVariable Integer idInvestidor
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Representantes do investidor encontrados", representanteInvestidorService.findByInvestidorId(idInvestidor)));
    }

    @GetMapping("/{idInvestidor}/socios")
    public ResponseEntity<ApiResponse<List<RepresentanteInvestidorResponseDTO>>> findSociosByInvestidor(
        @PathVariable Integer idInvestidor
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Socios do investidor encontrados", socioRepresentanteService.findSociosByInvestidorId(idInvestidor)));
    }

    @PostMapping("/{idInvestidor}/representantes")
    public ResponseEntity<ApiResponse<RepresentanteInvestidorResponseDTO>> associarRepresentante(
        @PathVariable Integer idInvestidor,
        @Valid @RequestBody AssociarRepresentanteRequestDTO dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok("Representante associado com sucesso", associarRepresentanteService.associar(idInvestidor, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Registo eliminado com sucesso", null));
    }
}
