package cv.zeemsv.api.interfaces.error;

import cv.zeemsv.api.application.audit.service.AppLogsService;
import cv.zeemsv.api.exceptions.BusinessException;
import cv.zeemsv.api.exceptions.ExternalApiException;
import cv.zeemsv.api.exceptions.OtpRequiredException;
import cv.zeemsv.api.interfaces.dto.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class ApiExceptionHandler {
    private final AppLogsService appLogsService;

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> notFound(EntityNotFoundException ex, HttpServletRequest request) {
        auditError(ex, HttpStatus.NOT_FOUND, request);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.fail(ex.getMessage(), null));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> business(BusinessException ex, HttpServletRequest request) {
        auditError(ex, HttpStatus.BAD_REQUEST, request);
        return ResponseEntity.badRequest().body(ApiResponse.fail(ex.getMessage(), null));
    }

    @ExceptionHandler(OtpRequiredException.class)
    public ResponseEntity<ApiResponse<Void>> otpRequired(OtpRequiredException ex) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(ApiResponse.ok(ex.getMessage(), null));
    }

    @ExceptionHandler(ExternalApiException.class)
    public ResponseEntity<ApiResponse<Void>> externalApi(ExternalApiException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());
        HttpStatus responseStatus = status != null ? status : HttpStatus.BAD_GATEWAY;
        auditError(ex, responseStatus, request);
        return ResponseEntity.status(responseStatus).body(ApiResponse.fail(ex.getMessage(), null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> validation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(e -> errors.put(e.getField(), e.getDefaultMessage()));
        auditError(ex, HttpStatus.BAD_REQUEST, request);
        return ResponseEntity.badRequest().body(ApiResponse.fail("Dados invalidos", errors));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> constraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(e -> errors.put(e.getPropertyPath().toString(), e.getMessage()));
        auditError(ex, HttpStatus.BAD_REQUEST, request);
        return ResponseEntity.badRequest().body(ApiResponse.fail("Dados invalidos", errors));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> unexpected(Exception ex, HttpServletRequest request) {
        auditError(ex, HttpStatus.INTERNAL_SERVER_ERROR, request);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.fail("Erro interno do servidor", null));
    }

    private void auditError(Exception ex, HttpStatus status, HttpServletRequest request) {
        appLogsService.saveErrorAsyncSafe(ex, status.value(), request);
    }
}
