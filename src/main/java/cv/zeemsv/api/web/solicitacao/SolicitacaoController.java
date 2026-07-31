package cv.zeemsv.api.web.solicitacao;

import cv.zeemsv.api.application.solicitacao.dto.SolicitacaoDocumentosRequisitosResponseDTO;
import cv.zeemsv.api.application.solicitacao.dto.CorrigirSolicitacaoRequestDTO;
import cv.zeemsv.api.application.solicitacao.dto.SolicitacaoDetailResponseDTO;
import cv.zeemsv.api.application.solicitacao.dto.SolicitacaoRequestDTO;
import cv.zeemsv.api.application.solicitacao.dto.SolicitacaoResponseDTO;
import cv.zeemsv.api.application.solicitacao.dto.SubmeterSolicitacaoRequestDTO;
import cv.zeemsv.api.application.solicitacao.dto.ReciboPedidoDadosResponseDTO;
import cv.zeemsv.api.application.solicitacao.service.SolicitacaoService;
import cv.zeemsv.api.interfaces.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/solicitacaos")
@RequiredArgsConstructor
public class SolicitacaoController {
    private final SolicitacaoService service;

    @PostMapping
    public ResponseEntity<ApiResponse<SolicitacaoResponseDTO>> create(@Valid @RequestBody SolicitacaoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Registo criado com sucesso", service.create(dto)));
    }

    @PostMapping(value = "/submeter", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<SolicitacaoResponseDTO>> submeter(
        @Valid @ModelAttribute SubmeterSolicitacaoRequestDTO dto,
        @RequestParam(name = "id_investidor", required = false) Integer idInvestidor,
        @RequestParam(name = "id_projecto", required = false) Integer idProjecto,
        @RequestParam(name = "ids_lote", required = false) String idsLote,
        @RequestHeader(name = "Authorization", required = false) String authorization
    ) {
        if (dto.getIdInvestidor() == null) {
            dto.setIdInvestidor(idInvestidor);
        }
        if (dto.getIdProjeto() == null) {
            dto.setIdProjeto(idProjecto);
        }
        if (dto.getIdsLote() == null) {
            dto.setIdsLote(idsLote);
        }
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok("Solicitacao submetida com sucesso", service.submeter(dto, authorization)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SolicitacaoResponseDTO>> update(@PathVariable Integer id, @Valid @RequestBody SolicitacaoRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok("Registo atualizado com sucesso", service.update(id, dto)));
    }

    @PutMapping(value = "/{id}/correcao", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<SolicitacaoResponseDTO>> corrigir(
        @PathVariable Integer id,
        @ModelAttribute CorrigirSolicitacaoRequestDTO dto,
        @RequestParam(name = "id_projeto", required = false) Integer idProjeto,
        @RequestParam(name = "id_projecto", required = false) Integer idProjecto,
        @RequestParam(name = "ids_lote", required = false) String idsLote
    ) {
        if (dto.getIdProjeto() == null) {
            dto.setIdProjeto(idProjeto != null ? idProjeto : idProjecto);
        }
        if (dto.getIdsLote() == null) {
            dto.setIdsLote(idsLote);
        }
        return ResponseEntity.ok(ApiResponse.ok("Solicitacao corrigida com sucesso", service.corrigir(id, dto)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SolicitacaoResponseDTO>> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok("Registo encontrado", service.findById(id)));
    }

    @GetMapping("/{id}/detalhes")
    public ResponseEntity<ApiResponse<SolicitacaoDetailResponseDTO>> findDetailById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok("Detalhes da solicitacao encontrados", service.findDetailById(id)));
    }

    @GetMapping("/{id}/recibo-dados")
    public ResponseEntity<ApiResponse<ReciboPedidoDadosResponseDTO>> findReciboDados(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok("Dados do recibo encontrados", service.findReciboDados(id)));
    }

    @GetMapping("/recibo-dados/processo/{nrProcesso}")
    public ResponseEntity<ApiResponse<ReciboPedidoDadosResponseDTO>> findReciboDadosByProcesso(@PathVariable BigDecimal nrProcesso) {
        return ResponseEntity.ok(ApiResponse.ok("Dados do recibo encontrados", service.findReciboDadosByProcesso(nrProcesso)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SolicitacaoResponseDTO>>> findAll() {
        return ResponseEntity.ok(ApiResponse.ok("Registos encontrados", service.findAll()));
    }

    @GetMapping("/investidor/{idInvestidor}")
    public ResponseEntity<ApiResponse<List<SolicitacaoResponseDTO>>> findByInvestidorId(@PathVariable Integer idInvestidor) {
        return ResponseEntity.ok(ApiResponse.ok("Solicitacoes do investidor encontradas", service.findByInvestidorId(idInvestidor)));
    }

    @GetMapping("/tipo-solicitacao/{idTpSolicitacao}/documentos")
    public ResponseEntity<ApiResponse<SolicitacaoDocumentosRequisitosResponseDTO>> findDocumentosByTipoSolicitacaoId(@PathVariable Integer idTpSolicitacao) {
        return ResponseEntity.ok(ApiResponse.ok("Documentos e requisitos do tipo de solicitacao encontrados", service.findDocumentosByTipoSolicitacaoId(idTpSolicitacao)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Registo eliminado com sucesso", null));
    }
}
