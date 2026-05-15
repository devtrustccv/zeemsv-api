package cv.zeemsv.api.web.atividade;

import cv.zeemsv.api.application.atividade.dto.AtividadeResponseDTO;
import cv.zeemsv.api.application.atividade.dto.NotificacaoInvestidorResponseDTO;
import cv.zeemsv.api.application.atividade.service.AtividadeService;
import cv.zeemsv.api.interfaces.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/atividades")
@RequiredArgsConstructor
public class AtividadeController {
    private final AtividadeService service;

    @GetMapping("/investidor/{idInvestidor}/notificacoes")
    public ResponseEntity<ApiResponse<List<NotificacaoInvestidorResponseDTO>>> findNotificacoesByInvestidorId(@PathVariable Integer idInvestidor) {
        return ResponseEntity.ok(ApiResponse.ok("Notificacoes do investidor encontradas", service.findNotificacoesByInvestidorId(idInvestidor)));
    }

    @GetMapping("/investidor/{idInvestidor}/agendadas")
    public ResponseEntity<ApiResponse<List<AtividadeResponseDTO>>> findAgendadasByInvestidorId(@PathVariable Integer idInvestidor) {
        return ResponseEntity.ok(ApiResponse.ok("Atividades agendadas do investidor encontradas", service.findAgendadasByInvestidorId(idInvestidor)));
    }
}
