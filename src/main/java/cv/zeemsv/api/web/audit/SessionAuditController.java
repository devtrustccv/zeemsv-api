package cv.zeemsv.api.web.audit;

import cv.zeemsv.api.application.audit.dto.SessionAuditPageResponseDTO;
import cv.zeemsv.api.application.audit.dto.SessionAuditRequestDTO;
import cv.zeemsv.api.application.audit.dto.SessionAuditResponseDTO;
import cv.zeemsv.api.application.audit.service.SessionAuditService;
import cv.zeemsv.api.interfaces.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auditorias/sessoes")
@RequiredArgsConstructor
public class SessionAuditController {
    private final SessionAuditService service;

    @PostMapping
    public ResponseEntity<ApiResponse<SessionAuditResponseDTO>> create(
        @RequestBody SessionAuditRequestDTO dto,
        HttpServletRequest request
    ) {
        applyRequestFallbacks(dto, request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok("Sessao registada com sucesso", service.create(dto)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<SessionAuditPageResponseDTO>> findAll(
        @RequestParam(required = false) String userId,
        @RequestParam(required = false) String state,
        @RequestParam(required = false) String authenticationMethod,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
        @RequestParam(required = false) String ip,
        @RequestParam(required = false) Integer page,
        @RequestParam(required = false) Integer size
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
            "Sessoes encontradas",
            service.findAll(userId, state, authenticationMethod, dateFrom, dateTo, ip, page, size)
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SessionAuditResponseDTO>> findById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok("Sessao encontrada", service.findById(id)));
    }

    @GetMapping("/estados")
    public ResponseEntity<ApiResponse<List<String>>> findStates() {
        return ResponseEntity.ok(ApiResponse.ok("Estados encontrados", service.findStates()));
    }

    @GetMapping("/utilizadores")
    public ResponseEntity<ApiResponse<List<String>>> findUsers() {
        return ResponseEntity.ok(ApiResponse.ok("Utilizadores encontrados", service.findUsers()));
    }

    private void applyRequestFallbacks(SessionAuditRequestDTO dto, HttpServletRequest request) {
        if (!StringUtils.hasText(dto.getIp())) {
            dto.setIp(resolveIp(request));
        }
        if (!StringUtils.hasText(dto.getUserAgent())) {
            dto.setUserAgent(request.getHeader("User-Agent"));
        }
    }

    private String resolveIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(forwardedFor)) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
