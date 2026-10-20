package cv.zeemsv.api.application.audit.service;

import cv.zeemsv.api.application.audit.entity.TransactionAuditLog;
import cv.zeemsv.api.infrastructure.entity.ZeeTCobrancaEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTPagamentoIntencaoCobrancaEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTSolicitacaoEntity;
import cv.zeemsv.api.infrastructure.repository.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionAuditContextResolverTest {
    @Mock ZeeTCobrancaRepository cobrancaRepository;
    @Mock ZeeTCobrancaPrestacaoRepository prestacaoRepository;
    @Mock ZeeTSolicitacaoRepository solicitacaoRepository;
    @Mock ZeeTSolicitacaoCobrancaRepository solicitacaoCobrancaRepository;
    @Mock ZeeTPagamentoRepository pagamentoRepository;
    @Mock ZeeTPagamentoIntencaoRepository intencaoRepository;
    @Mock ZeeTPagamentoIntencaoCobrancaRepository intencaoCobrancaRepository;
    @Mock ZeeTProjInvestRepository projetoRepository;
    @InjectMocks TransactionAuditContextResolver resolver;

    @Test
    void resolvesInvestorAndProcessUserWithoutChangingGatewayActor() {
        billing(1, 10, 20, "30");
        var log = event("payment-gateway", "/api/v1/pagamentos");
        log.setMetadata(Map.of("idCobranca", 1, "source", "business_service"));

        resolver.enrich(log);

        assertEquals("payment-gateway", log.getUserId());
        assertEquals("30", log.getRelatedUserId());
        assertEquals(10, log.getMetadata().get("idInvestidor"));
        assertEquals("business_service", log.getMetadata().get("source"));
        assertEquals(1, log.getContextVersion());
    }

    @Test
    void handlesMultipleInvestorsAndUsersInOneIntentionWithoutPickingAnArbitraryOwner() {
        billing(1, 10, 20, "30");
        billing(2, 11, 21, "31");
        var first = new ZeeTPagamentoIntencaoCobrancaEntity();
        first.setIdCobranca(1);
        var second = new ZeeTPagamentoIntencaoCobrancaEntity();
        second.setIdCobranca(2);
        when(intencaoCobrancaRepository.findByIdIntencaoOrderByIdAsc(5)).thenReturn(List.of(first, second));
        var log = event("payment-gateway", "/api/v1/pagamentos");
        log.setTableName("zee_t_pagamento_intencao");
        log.setTableId("5");

        resolver.enrich(log);

        assertEquals(List.of(10, 11), log.getMetadata().get("idsInvestidor"));
        assertEquals(List.of("30", "31"), log.getMetadata().get("relatedUserIds"));
        assertNull(log.getMetadata().get("idInvestidor"));
        assertNull(log.getRelatedUserId());
        assertEquals("payment-gateway", log.getUserId());
    }

    @Test
    void investorListRouteDoesNotConfuseInvestorIdWithSolicitationIdInLegacyLogs() {
        var log = event("30", "/api/v1/solicitacaos/investidor/10");
        log.setTableName("zee_t_solicitacao");
        log.setTableId("10");

        resolver.enrich(log);

        assertEquals(10, log.getMetadata().get("idInvestidor"));
        assertEquals("30", log.getRelatedUserId());
        verifyNoInteractions(solicitacaoRepository);
    }

    @Test
    void resolvesAutomaticDetailAndQueryReferencesAndIsIdempotent() {
        billing(1, 10, 20, "30");
        var log = event("40", "/api/v1/cobrancas/1?idInvestidor=10");
        resolver.enrich(log);
        Map<String, Object> first = Map.copyOf(log.getMetadata());

        resolver.enrich(log);

        assertEquals(first, log.getMetadata());
        assertEquals("40", log.getUserId());
        assertEquals("30", log.getRelatedUserId());
    }

    @Test
    void missingResourceDoesNotInventInvestorOrTreatGatewayAsRelatedUser() {
        var log = event("payment-gateway", "/api/v1/pagamentos/999");
        resolver.enrich(log);
        assertNull(log.getRelatedUserId());
        assertFalse(log.getMetadata().containsKey("idInvestidor"));
    }

    private void billing(int id, int investor, int solicitation, String user) {
        var billing = new ZeeTCobrancaEntity();
        billing.setIdInvestidor(investor);
        billing.setIdSolicitacao(solicitation);
        when(cobrancaRepository.findById(id)).thenReturn(Optional.of(billing));
        var request = new ZeeTSolicitacaoEntity();
        request.setIdInvestidor(investor);
        request.setUserSolic(user);
        when(solicitacaoRepository.findById(solicitation)).thenReturn(Optional.of(request));
    }

    private TransactionAuditLog event(String actor, String uri) {
        var log = new TransactionAuditLog();
        log.setUserId(actor);
        log.setRequestUri(uri);
        return log;
    }
}
