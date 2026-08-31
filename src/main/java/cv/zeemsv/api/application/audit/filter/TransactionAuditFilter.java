package cv.zeemsv.api.application.audit.filter;

import cv.zeemsv.api.application.audit.dto.TransactionAuditRequestDTO;
import cv.zeemsv.api.application.audit.service.TransactionAuditService;
import cv.zeemsv.api.domain.user.business.SessionBus;
import cv.zeemsv.api.domain.user.business.UserBus;
import cv.zeemsv.api.domain.user.model.SessionModel;
import cv.zeemsv.api.domain.user.model.UserModel;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
@RequiredArgsConstructor
public class TransactionAuditFilter extends OncePerRequestFilter {
    private static final String HEADER_SESSION_TOKEN = "X-SESSION-TOKEN";
    private static final String HEADER_FORWARDED_FOR = "X-Forwarded-For";
    private static final String HEADER_REAL_IP = "X-Real-IP";
    private static final String HEADER_USER_AGENT = "User-Agent";

    private final TransactionAuditService transactionAuditService;
    private final SessionBus sessionBus;
    private final UserBus userBus;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path == null
            || path.startsWith("/actuator")
            || path.startsWith("/swagger-ui")
            || path.startsWith("/v3/api-docs")
            || path.startsWith("/api/v1/auditorias");
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        long start = System.currentTimeMillis();
        try {
            filterChain.doFilter(request, response);
        } finally {
            auditRequest(request, response, System.currentTimeMillis() - start);
        }
    }

    private void auditRequest(HttpServletRequest request, HttpServletResponse response, long durationMs) {
        SessionModel session = resolveSession(request);
        UserModel user = resolveUser(session);

        TransactionAuditRequestDTO audit = new TransactionAuditRequestDTO();
        audit.setUserId(user != null ? String.valueOf(user.getId()) : userIdFromSession(session));
        audit.setUserName(user != null ? user.getName() : null);
        audit.setUserEmail(user != null ? user.getEmail() : null);
        audit.setIp(resolveIp(request));
        audit.setUserAgent(request.getHeader(HEADER_USER_AGENT));
        audit.setRequestMethod(request.getMethod());
        audit.setRequestUri(requestUri(request));
        audit.setStatusCode(response.getStatus());
        audit.setMetadata(metadata(request, session, durationMs));
        transactionAuditService.createAsyncSafe(audit);
    }

    private SessionModel resolveSession(HttpServletRequest request) {
        String sessionToken = request.getHeader(HEADER_SESSION_TOKEN);
        if (!StringUtils.hasText(sessionToken)) {
            return null;
        }
        return sessionBus.findBySessionToken(sessionToken.trim()).orElse(null);
    }

    private UserModel resolveUser(SessionModel session) {
        if (session == null || session.getUserId() == null) {
            return null;
        }
        return userBus.findById(session.getUserId()).orElse(null);
    }

    private String userIdFromSession(SessionModel session) {
        return session != null && session.getUserId() != null ? String.valueOf(session.getUserId()) : null;
    }

    private String requestUri(HttpServletRequest request) {
        String queryString = request.getQueryString();
        return StringUtils.hasText(queryString)
            ? request.getRequestURI() + "?" + queryString
            : request.getRequestURI();
    }

    private Map<String, Object> metadata(HttpServletRequest request, SessionModel session, long durationMs) {
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("durationMs", durationMs);
        metadata.put("queryString", request.getQueryString());
        metadata.put("sessionId", session != null ? session.getId() : null);
        metadata.put("sessionProvider", session != null ? session.getProvider() : null);
        return metadata;
    }

    private String resolveIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader(HEADER_FORWARDED_FOR);
        if (StringUtils.hasText(forwardedFor)) {
            return forwardedFor.split(",")[0].trim();
        }
        String realIp = request.getHeader(HEADER_REAL_IP);
        if (StringUtils.hasText(realIp)) {
            return realIp.trim();
        }
        return request.getRemoteAddr();
    }
}
