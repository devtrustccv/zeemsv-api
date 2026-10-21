package cv.zeemsv.api.application.audit.service;

import cv.zeemsv.api.application.audit.dto.AppLogRequestContext;
import cv.zeemsv.api.application.audit.entity.AppLogs;
import cv.zeemsv.api.config.AuditMongoProperties;
import cv.zeemsv.api.exceptions.BusinessException;
import cv.zeemsv.api.interfaces.error.ApiExceptionHandler;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayDeque;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.springframework.data.mongodb.core.MongoTemplate;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AppLogsServiceTest {
    @ParameterizedTest
    @CsvSource({"400, WARN", "500, ERROR"})
    void writesErrorAfterRequestIsRecycledWithoutAccessingServletRequest(int status, String level) {
        var mongo = mock(MongoTemplate.class);
        var worker = new AppLogsService(mongo, new AuditMongoProperties());
        var asyncService = mock(AppLogsService.class);
        var pending = new ArrayDeque<Runnable>();
        doAnswer(call -> {
            Throwable error = call.getArgument(0);
            Integer code = call.getArgument(1);
            AppLogRequestContext context = call.getArgument(2);
            pending.add(() -> worker.saveErrorAsyncSafe(error, code, context));
            return null;
        }).when(asyncService).saveErrorAsyncSafe(any(), anyInt(), any(AppLogRequestContext.class));
        when(mongo.insert(anyCollection(), eq("appLogs"))).thenAnswer(call -> call.getArgument(0));
        var request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/api/v1/pagamentos/manual");
        when(request.getQueryString()).thenReturn("idInvestidor=10");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(request.getHeader("User-Agent")).thenReturn("test-agent");
        var handler = new ApiExceptionHandler(asyncService);
        Exception error;
        if (status == 400) {
            var business = new BusinessException("Pagamento invalido");
            error = business;
            assertEquals(status, handler.business(business, request).getStatusCode().value());
        } else {
            error = new IllegalStateException("Falha original");
            assertEquals(status, handler.unexpected(error, request).getStatusCode().value());
        }
        verifyNoInteractions(mongo);

        // Simulate Tomcat recycling its facade before the queued task starts.
        reset(request);
        when(request.getMethod()).thenThrow(new IllegalStateException("request has been recycled"));
        clearInvocations(request);
        pending.remove().run();

        verifyNoInteractions(request);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<AppLogs>> logs = ArgumentCaptor.forClass(List.class);
        verify(mongo).insert(logs.capture(), eq("appLogs"));
        var saved = logs.getValue().get(0);
        assertEquals("POST", saved.getMethod());
        assertEquals("/api/v1/pagamentos/manual", saved.getPath());
        assertEquals("idInvestidor=10", saved.getQueryString());
        assertEquals("127.0.0.1", saved.getRemoteAddr());
        assertEquals("test-agent", saved.getUserAgent());
        assertEquals(status, saved.getStatus());
        assertEquals(level, saved.getLevel());
        assertEquals(error.getMessage(), saved.getMessage());
        assertEquals(error.getClass().getName(), saved.getExceptionClass());
        assertTrue(saved.getStackTrace().contains(error.getMessage()));
    }

    @Test
    void recordsErrorWithoutRequestContext() {
        var mongo = mock(MongoTemplate.class);
        when(mongo.insert(anyCollection(), eq("appLogs"))).thenAnswer(call -> call.getArgument(0));
        var service = new AppLogsService(mongo, new AuditMongoProperties());

        assertDoesNotThrow(() -> service.saveErrorAsyncSafe(new RuntimeException("failure"), 500,
            AppLogRequestContext.capture(null)));

        verify(mongo).insert(anyCollection(), eq("appLogs"));
    }
}
