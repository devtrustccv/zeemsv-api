package cv.zeemsv.api.web.atividade;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cv.zeemsv.api.application.atividade.dto.AtividadeResponseDTO;
import cv.zeemsv.api.application.atividade.dto.InteracaoRequestDTO;
import cv.zeemsv.api.application.atividade.dto.InteracaoMensagemResponseDTO;
import cv.zeemsv.api.application.atividade.dto.InteracaoRespostaRequestDTO;
import cv.zeemsv.api.application.atividade.dto.InteracaoResponseDTO;
import cv.zeemsv.api.application.atividade.dto.NotificacaoInvestidorResponseDTO;
import cv.zeemsv.api.application.atividade.dto.NotificacaoRespostaRequestDTO;
import cv.zeemsv.api.application.atividade.dto.NotificacaoRespostaResponseDTO;
import cv.zeemsv.api.application.atividade.service.AtividadeService;
import cv.zeemsv.api.exceptions.BusinessException;
import cv.zeemsv.api.interfaces.dto.ApiResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/atividades")
@RequiredArgsConstructor
public class AtividadeController {
    private final AtividadeService service;
    private final ObjectMapper objectMapper;
    private final Validator validator;

    @PostMapping(value = "/interacoes", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<AtividadeResponseDTO>> createInteracao(@Valid @RequestBody InteracaoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok("Interacao criada com sucesso", service.createInteracao(dto)));
    }

    @PostMapping(value = "/interacoes", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<AtividadeResponseDTO>> createInteracaoComAnexo(
        @RequestPart("interacao") String interacao,
        @RequestPart(value = "anexo", required = false) MultipartFile anexo
    ) {
        InteracaoRequestDTO dto = parseInteracao(interacao);
        validateInteracao(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok("Interacao criada com sucesso", service.createInteracao(dto, anexo)));
    }

    @GetMapping("/interacoes")
    public ResponseEntity<ApiResponse<List<InteracaoResponseDTO>>> findInteracoes(
        @RequestParam(name = "id_user", required = false) Integer idUser,
        @RequestParam(name = "id_investidor", required = false) Integer idInvestidor,
        @RequestParam(required = false) String email
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Interacoes encontradas", service.findInteracoes(idUser, idInvestidor, email)));
    }

    @PostMapping(value = "/interacoes/{idInteracao}/respostas", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<InteracaoMensagemResponseDTO>> responderInteracao(
        @PathVariable Integer idInteracao,
        @Valid @RequestBody InteracaoRespostaRequestDTO dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok("Resposta da interacao registada com sucesso", service.responderInteracao(idInteracao, dto, null)));
    }

    @PostMapping(value = "/interacoes/{idInteracao}/respostas", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<InteracaoMensagemResponseDTO>> responderInteracaoComAnexo(
        @PathVariable Integer idInteracao,
        @RequestPart("resposta") String resposta,
        @RequestPart(value = "anexo", required = false) MultipartFile anexo
    ) {
        InteracaoRespostaRequestDTO dto = parseInteracaoResposta(resposta);
        validateInteracaoResposta(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok("Resposta da interacao registada com sucesso", service.responderInteracao(idInteracao, dto, anexo)));
    }

    @GetMapping("/investidor/{idInvestidor}/notificacoes")
    public ResponseEntity<ApiResponse<List<NotificacaoInvestidorResponseDTO>>> findNotificacoesByInvestidorId(@PathVariable Integer idInvestidor) {
        return ResponseEntity.ok(ApiResponse.ok("Notificacoes do investidor encontradas", service.findNotificacoesByInvestidorId(idInvestidor)));
    }

    @PostMapping("/notificacoes/respostas")
    public ResponseEntity<ApiResponse<NotificacaoRespostaResponseDTO>> responderNotificacao(
        @Valid @RequestBody NotificacaoRespostaRequestDTO dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok("Resposta da notificacao enviada com sucesso", service.responderNotificacao(dto)));
    }

    @PatchMapping("/notificacoes/{idNotificacao}/leitura")
    public ResponseEntity<ApiResponse<Void>> markNotificacaoAsRead(
        @PathVariable Integer idNotificacao,
        @RequestParam(name = "id_user") Integer idUser
    ) {
        service.markNotificacaoAsRead(idNotificacao, idUser);
        return ResponseEntity.ok(ApiResponse.ok("Notificacao marcada como lida", null));
    }

    @GetMapping("/investidor/{idInvestidor}/agendadas")
    public ResponseEntity<ApiResponse<List<AtividadeResponseDTO>>> findAgendadasByInvestidorId(@PathVariable Integer idInvestidor) {
        return ResponseEntity.ok(ApiResponse.ok("Atividades agendadas do investidor encontradas", service.findAgendadasByInvestidorId(idInvestidor)));
    }

    private InteracaoRequestDTO parseInteracao(String interacao) {
        try {
            return objectMapper.readValue(interacao, InteracaoRequestDTO.class);
        } catch (JsonProcessingException ex) {
            throw new BusinessException("O campo interacao deve conter JSON valido.", ex);
        }
    }

    private void validateInteracao(InteracaoRequestDTO dto) {
        Set<ConstraintViolation<InteracaoRequestDTO>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    private InteracaoRespostaRequestDTO parseInteracaoResposta(String resposta) {
        try {
            return objectMapper.readValue(resposta, InteracaoRespostaRequestDTO.class);
        } catch (JsonProcessingException ex) {
            throw new BusinessException("O campo resposta deve conter JSON valido.", ex);
        }
    }

    private void validateInteracaoResposta(InteracaoRespostaRequestDTO dto) {
        Set<ConstraintViolation<InteracaoRespostaRequestDTO>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
