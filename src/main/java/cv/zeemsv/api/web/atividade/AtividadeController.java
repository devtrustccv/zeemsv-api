package cv.zeemsv.api.web.atividade;

import cv.zeemsv.api.application.atividade.dto.AtividadeResponseDTO;
import cv.zeemsv.api.application.atividade.dto.InteracaoRequestDTO;
import cv.zeemsv.api.application.atividade.dto.InteracaoResponseDTO;
import cv.zeemsv.api.application.atividade.dto.NotificacaoInvestidorResponseDTO;
import cv.zeemsv.api.application.atividade.service.AtividadeService;
import cv.zeemsv.api.interfaces.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/atividades")
@RequiredArgsConstructor
public class AtividadeController {
    private final AtividadeService service;

    @PostMapping("/interacoes")
    public ResponseEntity<ApiResponse<AtividadeResponseDTO>> createInteracao(@Valid @RequestBody InteracaoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok("Interacao criada com sucesso", service.createInteracao(dto)));
    }

    @GetMapping("/interacoes")
    public ResponseEntity<ApiResponse<List<InteracaoResponseDTO>>> findInteracoes(
        @RequestParam(name = "id_user", required = false) Integer idUser,
        @RequestParam(name = "id_investidor", required = false) Integer idInvestidor,
        @RequestParam(required = false) String email
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Interacoes encontradas", service.findInteracoes(idUser, idInvestidor, email)));
    }

    @GetMapping("/investidor/{idInvestidor}/notificacoes")
    public ResponseEntity<ApiResponse<List<NotificacaoInvestidorResponseDTO>>> findNotificacoesByInvestidorId(@PathVariable Integer idInvestidor) {
        return ResponseEntity.ok(ApiResponse.ok("Notificacoes do investidor encontradas", service.findNotificacoesByInvestidorId(idInvestidor)));
    }

    @GetMapping("/investidor/{idInvestidor}/agendadas")
    public ResponseEntity<ApiResponse<List<AtividadeResponseDTO>>> findAgendadasByInvestidorId(@PathVariable Integer idInvestidor) {
        return ResponseEntity.ok(ApiResponse.ok("Atividades agendadas do investidor encontradas", service.findAgendadasByInvestidorId(idInvestidor)));
    }
}
