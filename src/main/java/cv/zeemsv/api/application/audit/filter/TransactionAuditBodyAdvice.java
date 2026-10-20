package cv.zeemsv.api.application.audit.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/** Retains only resource identifiers from the parsed body, never the full payload. */
@ControllerAdvice
@RequiredArgsConstructor
public class TransactionAuditBodyAdvice extends RequestBodyAdviceAdapter {
    public static final String CONTEXT_ATTRIBUTE = TransactionAuditBodyAdvice.class.getName() + ".context";
    private static final Set<String> FIELDS = Set.of(
        "idInvestidor", "id_investidor", "idCobranca", "id_cobranca", "idsCobranca", "ids_cobranca",
        "idPrestacao", "id_prestacao", "idsPrestacao", "ids_prestacao", "idSolicitacao", "id_solicitacao",
        "idProjeto", "id_projeto", "idPagamento", "id_pagamento", "intentionId", "transactionId"
    );
    private final ObjectMapper objectMapper;

    @Override
    public boolean supports(MethodParameter parameter, Type type, Class<? extends HttpMessageConverter<?>> converter) {
        return parameter.getContainingClass().getPackageName().startsWith("cv.zeemsv.api.web.");
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage input, MethodParameter parameter,
                                Type type, Class<? extends HttpMessageConverter<?>> converter) {
        var attributes = RequestContextHolder.getRequestAttributes();
        var servletRequest = input instanceof ServletServerHttpRequest request ? request.getServletRequest()
            : attributes instanceof ServletRequestAttributes servlet ? servlet.getRequest() : null;
        if (servletRequest != null) {
            try {
                JsonNode tree = objectMapper.valueToTree(body);
                Map<String, Object> context = new LinkedHashMap<>();
                FIELDS.forEach(field -> {
                    JsonNode value = tree.get(field);
                    if (value != null && !value.isNull()) {
                        context.put(field, objectMapper.convertValue(value, Object.class));
                    }
                });
                servletRequest.setAttribute(CONTEXT_ATTRIBUTE, context);
            } catch (IllegalArgumentException ignored) {
                // Audit context must not change request handling.
            }
        }
        return body;
    }
}
