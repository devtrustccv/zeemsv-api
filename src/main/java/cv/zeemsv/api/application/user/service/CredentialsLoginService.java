package cv.zeemsv.api.application.user.service;

import cv.zeemsv.api.application.audit.dto.AccessAuditRequestDTO;
import cv.zeemsv.api.application.audit.dto.SessionAuditRequestDTO;
import cv.zeemsv.api.application.audit.service.AccessAuditService;
import cv.zeemsv.api.application.audit.service.SessionAuditService;
import cv.zeemsv.api.application.user.dto.CredentialsLoginRequestDTO;
import cv.zeemsv.api.application.user.dto.LoginResponseDTO;
import cv.zeemsv.api.domain.user.business.SessionBus;
import cv.zeemsv.api.domain.user.business.UserBus;
import cv.zeemsv.api.domain.user.model.SessionModel;
import cv.zeemsv.api.domain.user.model.UserModel;
import cv.zeemsv.api.exceptions.BusinessException;
import cv.zeemsv.api.infrastructure.repository.ZeeTRepresInvestidorRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTSocioRepresRepository;
import cv.zeemsv.api.utils.JwtUtil;
import cv.zeemsv.api.utils.enums.DefaultStatusApp;
import cv.zeemsv.api.utils.enums.LoginProvider;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@RequiredArgsConstructor
public class CredentialsLoginService {
    private final UserBus userBus;
    private final SessionBus sessionBus;
    private final PasswordEncoder passwordEncoder;
    private final ZeeTSocioRepresRepository socioRepresRepository;
    private final ZeeTRepresInvestidorRepository represInvestidorRepository;
    private final AccessAuditService accessAuditService;
    private final SessionAuditService sessionAuditService;

    @Value("${application.session.jwt-secret:01234567890123456789012345678901}")
    private String jwtSecret;

    @Value("${application.session.jwt-expiration-in-hours:8}")
    private long jwtExpirationInHours;

    @Transactional
    public LoginResponseDTO login(CredentialsLoginRequestDTO request, String fingerprint) {
        return login(request, fingerprint, null, null);
    }

    @Transactional
    public LoginResponseDTO login(CredentialsLoginRequestDTO request, String fingerprint, String ip, String userAgent) {
        String email = request.getEmail().trim().toLowerCase();
        UserModel user = userBus.findByEmail(email).orElse(null);
        if (user == null) {
            auditLoginFailure(email, null, null, LoginProvider.LOCAL.name(), ip, userAgent);
            throw new BusinessException("Credenciais invalidas.", new RuntimeException("Credenciais invalidas."));
        }

        if (user.getPasswordHash() == null || !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            auditLoginFailure(email, user.getName(), user.getEmail(), LoginProvider.LOCAL.name(), ip, userAgent);
            throw new BusinessException("Credenciais invalidas.", new RuntimeException("Credenciais invalidas."));
        }

        String jwtToken = JwtUtil.generateToken(
            jwtSecret,
            user.getEmail(),
            jwtExpirationInHours,
            user.getEmail(),
            user.getName(),
            fingerprint
        );

        SessionModel session = SessionModel.builder()
            .userId(user.getId())
            .status(DefaultStatusApp.ATIVO.getKey())
            .startDate(LocalDateTime.now())
            .endDate(LocalDateTime.now().plusHours(jwtExpirationInHours))
            .sessionToken(jwtToken)
            .provider(LoginProvider.LOCAL.name())
            .build();
        session = sessionBus.save(session);

        var socioRepres = socioRepresRepository.findFirstByIdUserOrderByIdDesc(user.getId()).orElse(null);
        boolean hasInvestidorRelation = socioRepres != null
            && represInvestidorRepository.existsByIdSocioRepres(socioRepres.getId());

        LoginResponseDTO response = LoginResponseDTO.builder()
            .sessionToken(jwtToken)
            .userId(user.getId())
            .idSocioRepres(socioRepres != null ? socioRepres.getId() : null)
            .role(hasInvestidorRelation ? "INVESTIDOR" : "none_investidor")
            .email(user.getEmail())
            .nome(user.getName())
            .status(user.getStatus())
            .subCmdcv(user.getSubCmdcv())
            .build();
        auditLoginSuccess(response, session, LoginProvider.LOCAL.name(), ip, userAgent);
        return response;
    }

    private void auditLoginSuccess(LoginResponseDTO response, SessionModel session, String provider, String ip, String userAgent) {
        runAfterCommit(() -> {
            accessAuditService.createAsyncSafe(accessAudit(
                response.getUserId(),
                response.getNome(),
                response.getEmail(),
                response.getEmail(),
                "LOGIN_SUCCESS",
                "Login com sucesso",
                provider,
                ip,
                userAgent,
                200
            ));
            SessionAuditRequestDTO sessionAudit = new SessionAuditRequestDTO();
            sessionAudit.setUserId(toStringValue(response.getUserId()));
            sessionAudit.setUserName(response.getNome());
            sessionAudit.setUserEmail(response.getEmail());
            sessionAudit.setIp(ip);
            sessionAudit.setAuthenticationMethod(provider);
            sessionAudit.setState("ATIVA");
            sessionAudit.setStartedAt(session.getStartDate());
            sessionAudit.setExpiresAt(session.getEndDate());
            sessionAudit.setSessionId(toStringValue(session.getId()));
            sessionAudit.setUserAgent(userAgent);
            sessionAuditService.createAsyncSafe(sessionAudit);
        });
    }

    private void auditLoginFailure(String identifier, String userName, String userEmail, String provider, String ip, String userAgent) {
        accessAuditService.createAsyncSafe(accessAudit(
            null,
            userName,
            userEmail,
            identifier,
            "LOGIN_FAILED",
            "Login falhado",
            provider,
            ip,
            userAgent,
            401
        ));
    }

    private AccessAuditRequestDTO accessAudit(
        Object userId,
        String userName,
        String userEmail,
        String identifier,
        String eventType,
        String eventLabel,
        String provider,
        String ip,
        String userAgent,
        Integer statusCode
    ) {
        AccessAuditRequestDTO dto = new AccessAuditRequestDTO();
        dto.setUserId(toStringValue(userId));
        dto.setUserName(userName);
        dto.setUserEmail(userEmail);
        dto.setIdentifier(identifier);
        dto.setEventType(eventType);
        dto.setEventLabel(eventLabel);
        dto.setAuthenticationMethod(provider);
        dto.setIp(ip);
        dto.setUserAgent(userAgent);
        dto.setStatusCode(statusCode);
        return dto;
    }

    private String toStringValue(Object value) {
        return value != null ? String.valueOf(value) : null;
    }

    private void runAfterCommit(Runnable action) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    action.run();
                }
            });
            return;
        }
        action.run();
    }
}
