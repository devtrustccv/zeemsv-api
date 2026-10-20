package cv.zeemsv.api.web.audit;

import cv.zeemsv.api.application.audit.dto.TransactionAuditRequestDTO;
import cv.zeemsv.api.application.audit.filter.TransactionAuditBodyAdvice;
import cv.zeemsv.api.application.audit.filter.TransactionAuditFilter;
import cv.zeemsv.api.application.audit.service.TransactionAuditService;
import cv.zeemsv.api.domain.user.business.SessionBus;
import cv.zeemsv.api.domain.user.business.UserBus;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TransactionAuditFilterTest {
    @Test
    void preservesResourceContextCapturedBeforeDeletion() throws Exception {
        var service = mock(TransactionAuditService.class);
        when(service.contextBeforeDeletion("/api/v1/projetos/1"))
            .thenReturn(Map.of("idInvestidor", 10, "relatedUserIds", java.util.List.of("30")));
        var filter = new TransactionAuditFilter(service, mock(SessionBus.class), mock(UserBus.class));
        var mvc = MockMvcBuilders.standaloneSetup(new FixtureController()).addFilters(filter).build();

        mvc.perform(delete("/api/v1/projetos/1")).andExpect(status().isOk());

        var dto = ArgumentCaptor.forClass(TransactionAuditRequestDTO.class);
        verify(service).createAsyncSafe(dto.capture());
        assertEquals(10, dto.getValue().getMetadata().get("idInvestidor"));
        assertEquals(java.util.List.of("30"), dto.getValue().getMetadata().get("relatedUserIds"));
    }

    @Test
    void retainsOnlyResourceIdsFromParsedJsonForAutomaticAudit() throws Exception {
        var service = mock(TransactionAuditService.class);
        var filter = new TransactionAuditFilter(service, mock(SessionBus.class), mock(UserBus.class));
        var mvc = MockMvcBuilders.standaloneSetup(new FixtureController())
            .setControllerAdvice(new TransactionAuditBodyAdvice(new ObjectMapper()))
            .addFilters(filter).build();

        mvc.perform(post("/api/v1/pagamentos/realizar-pagamento").contentType(MediaType.APPLICATION_JSON)
            .content("{\"ids_cobranca\":[1,2],\"id_investidor\":10,\"password\":\"private\",\"exposicao\":\"private\"}"))
            .andExpect(status().isOk());

        var dto = ArgumentCaptor.forClass(TransactionAuditRequestDTO.class);
        verify(service).createAsyncSafe(dto.capture());
        assertEquals(java.util.List.of(1, 2), dto.getValue().getMetadata().get("ids_cobranca"));
        assertEquals(10, dto.getValue().getMetadata().get("id_investidor"));
        assertFalse(dto.getValue().getMetadata().containsKey("password"));
        assertFalse(dto.getValue().getMetadata().containsKey("exposicao"));
    }

    @Test
    void formCallbackRetainsTransactionReference() throws Exception {
        var service = mock(TransactionAuditService.class);
        var filter = new TransactionAuditFilter(service, mock(SessionBus.class), mock(UserBus.class));
        var mvc = MockMvcBuilders.standaloneSetup(new FixtureController()).addFilters(filter).build();

        mvc.perform(post("/api/v1/pagamentos/confirmar-pagamentos")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED).param("transactionId", "tx-123"))
            .andExpect(status().isOk());

        var dto = ArgumentCaptor.forClass(TransactionAuditRequestDTO.class);
        verify(service).createAsyncSafe(dto.capture());
        assertEquals("tx-123", dto.getValue().getMetadata().get("transactionId"));
    }

    @RestController
    static class FixtureController {
        @DeleteMapping("/api/v1/projetos/1")
        public String deleteProject() { return "ok"; }

        @PostMapping("/api/v1/pagamentos/realizar-pagamento")
        public String create(@RequestBody Map<String, Object> body) { return "ok"; }

        @PostMapping("/api/v1/pagamentos/confirmar-pagamentos")
        public String callback() { return "ok"; }
    }
}
