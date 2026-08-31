package cv.zeemsv.api.web.audit;

import cv.zeemsv.api.application.audit.dto.AccessAuditPageResponseDTO;
import cv.zeemsv.api.application.audit.dto.AccessAuditRequestDTO;
import cv.zeemsv.api.application.audit.dto.AccessAuditResponseDTO;
import cv.zeemsv.api.application.audit.service.AccessAuditService;
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
@RequestMapping("/api/v1/auditorias/logs-acesso")
@RequiredArgsConstructor
public class AccessAuditController {
    private final AccessAuditService service;

    @PostMapping
    public ResponseEntity<ApiResponse<AccessAuditResponseDTO>> create(
        @RequestBody AccessAuditRequestDTO dto,
        HttpServletRequest request
    ) {
        applyRequestFallbacks(dto, request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok("Log de acesso registado com sucesso", service.create(dto)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<AccessAuditPageResponseDTO>> findAll(
        @RequestParam(required = false) String userId,
        @RequestParam(required = false) String identifier,
        @RequestParam(required = false) String eventType,
        @RequestParam(required = false) String authenticationMethod,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
        @RequestParam(required = false) String ip,
        @RequestParam(required = false) Integer page,
        @RequestParam(required = false) Integer size
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
            "Logs de acesso encontrados",
            service.findAll(userId, identifier, eventType, authenticationMethod, dateFrom, dateTo, ip, page, size)
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AccessAuditResponseDTO>> findById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok("Log de acesso encontrado", service.findById(id)));
    }

    @GetMapping("/tipos-evento")
    public ResponseEntity<ApiResponse<List<String>>> findEventTypes() {
        return ResponseEntity.ok(ApiResponse.ok("Tipos de evento encontrados", service.findEventTypes()));
    }

    @GetMapping("/utilizadores")
    public ResponseEntity<ApiResponse<List<String>>> findUsers() {
        return ResponseEntity.ok(ApiResponse.ok("Utilizadores encontrados", service.findUsers()));
    }

    private void applyRequestFallbacks(AccessAuditRequestDTO dto, HttpServletRequest request) {
        if (!StringUtils.hasText(dto.getIp())) {
            dto.setIp(resolveIp(request));
        }
        if (!StringUtils.hasText(dto.getUserAgent())) {
            dto.setUserAgent(request.getHeader("User-Agent"));
        }
        if (!StringUtils.hasText(dto.getRequestUri())) {
            dto.setRequestUri(request.getRequestURI());
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
