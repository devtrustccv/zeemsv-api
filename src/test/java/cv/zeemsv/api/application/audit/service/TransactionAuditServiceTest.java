package cv.zeemsv.api.application.audit.service;

import cv.zeemsv.api.application.audit.dto.TransactionAuditRequestDTO;
import cv.zeemsv.api.application.audit.entity.TransactionAuditLog;
import cv.zeemsv.api.config.AuditMongoProperties;
import cv.zeemsv.api.config.AuditProperties;
import cv.zeemsv.api.infrastructure.repository.ZeeTUserRepository;
import java.util.List;
import java.util.Map;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionAuditServiceTest {
    @Mock MongoTemplate mongo;
    @Mock ZeeTUserRepository users;
    @Mock TransactionAuditContextResolver resolver;
    TransactionAuditService service;

    @BeforeEach
    void setup() {
        service = new TransactionAuditService(mongo, users, new AuditMongoProperties(), new AuditProperties(), resolver);
    }

    @Test
    void combinesActorRelatedUserAndInvestorFiltersWithoutBroadeningActorMatch() {
        when(mongo.find(any(Query.class), eq(TransactionAuditLog.class), anyString())).thenReturn(List.of());

        service.findAll("40", "30", null, null, null, null, null, null, 10, null, 0, 20);

        var query = ArgumentCaptor.forClass(Query.class);
        verify(mongo).count(query.capture(), eq(TransactionAuditLog.class), eq("transactionAuditLogs"));
        var filters = query.getValue().getQueryObject().getList("$and", Document.class);
        assertEquals(new Document("userId", "40"), filters.get(0));
        assertEquals(new Document("$or", List.of(new Document("relatedUserId", "30"),
            new Document("metadata.relatedUserIds", "30"))), filters.get(1));
        assertEquals(new Document("$or", List.of(new Document("metadata.idInvestidor", 10),
            new Document("metadata.idsInvestidor", 10))), filters.get(2));
    }

    @Test
    void savesAndReturnsRelatedUserAndPreservesOriginalAuditIfResolutionFails() {
        var dto = new TransactionAuditRequestDTO();
        dto.setUserId("payment-gateway");
        dto.setRelatedUserId("30");
        dto.setMetadata(Map.of("idInvestidor", 10));
        doThrow(new IllegalStateException("resource unavailable")).when(resolver).enrich(any());
        when(mongo.insert(any(TransactionAuditLog.class), anyString())).thenAnswer(call -> call.getArgument(0));

        var response = service.create(dto);

        assertEquals("payment-gateway", response.getUserId());
        assertEquals("30", response.getRelatedUserId());
        assertEquals(10, response.getMetadata().get("idInvestidor"));
    }

    @Test
    void backfillUsesBatchesAndOnlyUpdatesContextFields() {
        var entry = new TransactionAuditLog();
        entry.setId(new ObjectId());
        entry.setUserId("payment-gateway");
        entry.setMetadata(Map.of("source", "legacy"));
        when(mongo.find(any(Query.class), eq(TransactionAuditLog.class), anyString()))
            .thenReturn(List.of(entry), List.of());
        doAnswer(call -> {
            TransactionAuditLog log = call.getArgument(0);
            log.setRelatedUserId("30");
            log.setMetadata(Map.of("source", "legacy", "idInvestidor", 10, "idsInvestidor", List.of(10)));
            log.setContextVersion(1);
            return null;
        }).when(resolver).enrich(entry);

        service.backfillContext();

        var update = ArgumentCaptor.forClass(Update.class);
        verify(mongo).updateFirst(any(Query.class), update.capture(), eq("transactionAuditLogs"));
        var fields = update.getValue().getUpdateObject().get("$set", Document.class);
        assertEquals("30", fields.get("relatedUserId"));
        assertEquals(10, fields.get("metadata.idInvestidor"));
        assertFalse(fields.containsKey("userId"));
        assertFalse(fields.containsKey("metadata"));
        assertFalse(fields.containsKey("date"));
        assertEquals("payment-gateway", entry.getUserId());
        var queries = ArgumentCaptor.forClass(Query.class);
        verify(mongo, times(2)).find(queries.capture(), eq(TransactionAuditLog.class), anyString());
        assertEquals(200, queries.getAllValues().get(0).getLimit());
        assertTrue(queries.getAllValues().get(1).getQueryObject().containsKey("_id"));
    }
}
