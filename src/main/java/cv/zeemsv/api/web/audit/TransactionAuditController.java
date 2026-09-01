package cv.zeemsv.api.web.audit;

import cv.zeemsv.api.application.audit.dto.TransactionAuditPageResponseDTO;
import cv.zeemsv.api.application.audit.dto.TransactionAuditRequestDTO;
import cv.zeemsv.api.application.audit.dto.TransactionAuditResponseDTO;
import cv.zeemsv.api.application.audit.service.TransactionAuditService;
import cv.zeemsv.api.interfaces.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
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
@RequestMapping("/api/v1/auditorias/transacoes")
@RequiredArgsConstructor
public class TransactionAuditController {
    private final TransactionAuditService service;

    @PostMapping
    public ResponseEntity<ApiResponse<TransactionAuditResponseDTO>> create(
        @Valid @RequestBody TransactionAuditRequestDTO dto,
        HttpServletRequest request
    ) {
        applyRequestFallbacks(dto, request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok("Auditoria transacional registada com sucesso", service.create(dto)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<TransactionAuditPageResponseDTO>> findAll(
        @RequestParam(required = false) String userId,
        @RequestParam(required = false) String actionType,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
        @RequestParam(required = false) String tableName,
        @RequestParam(required = false) String tableId,
        @RequestParam(required = false) String module,
        @RequestParam(required = false) Integer idInvestidor,
        @RequestParam(required = false) String ip,
        @RequestParam(required = false) Integer page,
        @RequestParam(required = false) Integer size
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
            "Auditorias transacionais encontradas",
            service.findAll(userId, actionType, dateFrom, dateTo, tableName, tableId, module, idInvestidor, ip, page, size)
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TransactionAuditResponseDTO>> findById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok("Auditoria transacional encontrada", service.findById(id)));
    }

    @GetMapping("/tipos-acao")
    public ResponseEntity<ApiResponse<List<String>>> findActionTypes() {
        return ResponseEntity.ok(ApiResponse.ok("Tipos de acao encontrados", service.findActionTypes()));
    }

    @GetMapping("/utilizadores")
    public ResponseEntity<ApiResponse<List<String>>> findUsers() {
        return ResponseEntity.ok(ApiResponse.ok("Utilizadores encontrados", service.findUsers()));
    }

    private void applyRequestFallbacks(TransactionAuditRequestDTO dto, HttpServletRequest request) {
        if (!StringUtils.hasText(dto.getIp())) {
            dto.setIp(resolveIp(request));
        }
        if (!StringUtils.hasText(dto.getUserAgent())) {
            dto.setUserAgent(request.getHeader("User-Agent"));
        }
        if (!StringUtils.hasText(dto.getRequestMethod())) {
            dto.setRequestMethod(request.getMethod());
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
