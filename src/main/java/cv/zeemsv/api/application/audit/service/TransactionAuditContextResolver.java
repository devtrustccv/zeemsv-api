package cv.zeemsv.api.application.audit.service;

import cv.zeemsv.api.application.audit.entity.TransactionAuditLog;
import cv.zeemsv.api.infrastructure.repository.ZeeTCobrancaPrestacaoRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTCobrancaRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTPagamentoIntencaoCobrancaRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTPagamentoIntencaoRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTPagamentoRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTProjInvestRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTSolicitacaoCobrancaRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTSolicitacaoRepository;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/** Resolves resource relationships without changing the actor recorded on the event. */
@Component
@RequiredArgsConstructor
public class TransactionAuditContextResolver {
    public static final int CONTEXT_VERSION = 1;
    private final ZeeTCobrancaRepository cobrancaRepository;
    private final ZeeTCobrancaPrestacaoRepository prestacaoRepository;
    private final ZeeTSolicitacaoRepository solicitacaoRepository;
    private final ZeeTSolicitacaoCobrancaRepository solicitacaoCobrancaRepository;
    private final ZeeTPagamentoRepository pagamentoRepository;
    private final ZeeTPagamentoIntencaoRepository intencaoRepository;
    private final ZeeTPagamentoIntencaoCobrancaRepository intencaoCobrancaRepository;
    private final ZeeTProjInvestRepository projetoRepository;

    public void enrich(TransactionAuditLog log) {
        Map<String, Object> metadata = log.getMetadata() == null
            ? new LinkedHashMap<>() : new LinkedHashMap<>(log.getMetadata());
        Context context = new Context();
        context.user(log.getRelatedUserId());
        values(metadata.get("relatedUserIds"), value -> context.user(String.valueOf(value)));
        values(metadata.get("idInvestidor"), value -> context.investor(integer(value)));
        values(metadata.get("idsInvestidor"), value -> context.investor(integer(value)));
        context.references(metadata);
        String uri = log.getRequestUri();
        boolean investorRoute = false;
        if (StringUtils.hasText(uri)) {
            String[] parts = uri.split("\\?", 2);
            String[] path = parts[0].split("/");
            for (int i = 0; i + 1 < path.length; i++) {
                String resource = path[i];
                if ("intencoes".equals(resource)) {
                    context.intention(path[i + 1]);
                }
                Integer id = integer(path[i + 1]);
                if (id == null) {
                    continue;
                }
                if (Set.of("investidor", "investidors", "investidores").contains(resource)) {
                    context.investor(id);
                    investorRoute = true;
                } else {
                    context.resource(resource, id);
                }
            }
            if (parts.length == 2) {
                Map<String, Object> parameters = new LinkedHashMap<>();
                for (String parameter : parts[1].split("&")) {
                    String[] pair = parameter.split("=", 2);
                    if (pair.length == 2) {
                        parameters.put(decode(pair[0]), decode(pair[1]));
                    }
                }
                context.references(parameters);
            }
        }
        // Legacy automatic logs inferred tableId from the first number in the URL,
        // which on /solicitacaos/investidor/{id} is the investor, not a solicitation.
        if (!investorRoute) {
            context.resource(log.getTableName(), integer(log.getTableId()));
        }
        if (context.users.isEmpty()) {
            context.user(log.getUserId());
        }
        if (!context.investors.isEmpty()) {
            metadata.put("idsInvestidor", context.investors.stream().toList());
            if (context.investors.size() == 1 && metadata.get("idInvestidor") == null) {
                metadata.put("idInvestidor", context.investors.iterator().next());
            }
        }
        if (!context.users.isEmpty()) {
            metadata.put("relatedUserIds", context.users.stream().toList());
            if (!StringUtils.hasText(log.getRelatedUserId()) && context.users.size() == 1) {
                log.setRelatedUserId(context.users.iterator().next());
            }
        }
        log.setMetadata(metadata);
        log.setContextVersion(CONTEXT_VERSION);
    }

    private static String decode(String value) {
        try {
            return URLDecoder.decode(value, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException ex) {
            return value;
        }
    }

    private static Integer integer(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return Integer.valueOf(value.toString().trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private static void values(Object value, Consumer<Object> consumer) {
        if (value instanceof Collection<?> collection) {
            collection.forEach(item -> values(item, consumer));
        } else if (value != null) {
            consumer.accept(value);
        }
    }

    private class Context {
        private final Set<Integer> investors = new LinkedHashSet<>();
        private final Set<String> users = new LinkedHashSet<>();
        private final Set<String> visited = new LinkedHashSet<>();

        void investor(Integer id) {
            if (id != null) investors.add(id);
        }

        void user(String id) {
            if (StringUtils.hasText(id) && !Set.of("payment-gateway", "system").contains(id.trim())) {
                users.add(id.trim());
            }
        }

        void references(Map<String, Object> data) {
            for (String key : Set.of("idInvestidor", "id_investidor", "idsInvestidor")) {
                values(data.get(key), value -> investor(integer(value)));
            }
            references(data, "cobranca", "idCobranca", "id_cobranca", "idsCobranca", "ids_cobranca");
            references(data, "prestacao", "idPrestacao", "id_prestacao", "idsPrestacao", "ids_prestacao");
            references(data, "solicitacao", "idSolicitacao", "id_solicitacao");
            references(data, "projeto", "idProjeto", "id_projeto");
            references(data, "pagamento", "idPagamento", "id_pagamento", "idsPagamento");
            references(data, "pagamento_intencao", "idIntencao");
            Object intentionId = data.get("intentionId");
            if (intentionId != null) {
                intention(String.valueOf(intentionId));
            }
            Object transactionId = data.get("transactionId");
            if (transactionId != null && visited.add("transaction:" + transactionId)) {
                intencaoRepository.findByTransactionId(String.valueOf(transactionId))
                    .ifPresent(entity -> resource("pagamento_intencao", entity.getId()));
            }
        }

        private void references(Map<String, Object> data, String resource, String... keys) {
            for (String key : keys) {
                values(data.get(key), value -> resource(resource, integer(value)));
            }
        }

        void intention(String id) {
            if (visited.add("intention:" + id)) {
                intencaoRepository.findByIntentionId(id)
                    .ifPresent(entity -> resource("pagamento_intencao", entity.getId()));
            }
        }

        void resource(String name, Integer id) {
            if (name == null || id == null) return;
            String resource = switch (name) {
                case "zee_t_cobranca", "cobrancas", "cobranca" -> "cobranca";
                case "zee_t_cobranca_prestacao", "prestacoes", "prestacao" -> "prestacao";
                case "zee_t_solicitacao", "solicitacaos", "solicitacoes", "solicitacao" -> "solicitacao";
                case "zee_t_proj_invest", "zee_t_projeto", "projetos", "projeto" -> "projeto";
                case "zee_t_pagamento", "pagamentos", "pagamento" -> "pagamento";
                case "zee_t_pagamento_intencao", "pagamento_intencao" -> "pagamento_intencao";
                case "zee_t_pagamento_intencao_cobranca" -> "pagamento_intencao_cobranca";
                case "zee_t_investidor" -> "investidor";
                default -> null;
            };
            if (resource == null || !visited.add(resource + ":" + id)) return;
            switch (resource) {
                case "investidor" -> investor(id);
                case "solicitacao" -> solicitacaoRepository.findById(id).ifPresent(entity -> {
                    investor(entity.getIdInvestidor());
                    user(entity.getUserSolic());
                });
                case "projeto" -> projetoRepository.findById(id).ifPresent(entity -> {
                    investor(entity.getIdInvestidor());
                    if (entity.getUserCreate() != null) user(entity.getUserCreate().toPlainString());
                });
                case "cobranca" -> cobrancaRepository.findById(id).ifPresent(entity -> {
                    investor(entity.getIdInvestidor());
                    resource("solicitacao", entity.getIdSolicitacao());
                    solicitacaoCobrancaRepository.findByIdCobranca(id).forEach(link -> resource("solicitacao", link.getIdSolicitacao()));
                    if (entity.getIdSolicitacao() == null) resource("projeto", entity.getIdProjeto());
                });
                case "prestacao" -> prestacaoRepository.findById(id)
                    .ifPresent(entity -> resource("cobranca", entity.getIdCobranca()));
                case "pagamento" -> pagamentoRepository.findById(id).ifPresent(entity -> {
                    investor(entity.getIdInvestidor());
                    resource("cobranca", entity.getIdCobranca());
                    resource("prestacao", entity.getIdPrestacao());
                    resource("solicitacao", entity.getIdSolicitacao());
                });
                case "pagamento_intencao" -> intencaoCobrancaRepository.findByIdIntencaoOrderByIdAsc(id)
                    .forEach(entity -> resource("cobranca", entity.getIdCobranca()));
                case "pagamento_intencao_cobranca" -> intencaoCobrancaRepository.findById(id)
                    .ifPresent(entity -> resource("cobranca", entity.getIdCobranca()));
                default -> { }
            }
        }
    }
}
