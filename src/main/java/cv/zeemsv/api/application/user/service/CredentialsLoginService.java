package cv.zeemsv.api.application.user.service;

import cv.zeemsv.api.application.user.dto.CredentialsLoginRequestDTO;
import cv.zeemsv.api.application.user.dto.LoginResponseDTO;
import cv.zeemsv.api.domain.user.business.SessionBus;
import cv.zeemsv.api.domain.user.business.UserBus;
import cv.zeemsv.api.domain.user.model.SessionModel;
import cv.zeemsv.api.domain.user.model.UserModel;
import cv.zeemsv.api.exceptions.BusinessException;
import cv.zeemsv.api.utils.JwtUtil;
import cv.zeemsv.api.utils.enums.DefaultStatusApp;
import cv.zeemsv.api.utils.enums.LoginProvider;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CredentialsLoginService {
    private final UserBus userBus;
    private final SessionBus sessionBus;
    private final PasswordEncoder passwordEncoder;

    @Value("${application.session.jwt-secret:01234567890123456789012345678901}")
    private String jwtSecret;

    @Value("${application.session.jwt-expiration-in-hours:8}")
    private long jwtExpirationInHours;

    @Transactional
    public LoginResponseDTO login(CredentialsLoginRequestDTO request, String fingerprint) {
        String email = request.getEmail().trim().toLowerCase();
        UserModel user = userBus.findByEmail(email)
            .orElseThrow(() -> new BusinessException("Credenciais invalidas.",
                new RuntimeException("Credenciais invalidas.")));

        if (user.getPasswordHash() == null || !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
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
        sessionBus.save(session);

        return LoginResponseDTO.builder()
            .sessionToken(jwtToken)
            .userId(user.getId())
            .pessoaId(user.getPessoaId())
            .email(user.getEmail())
            .nome(user.getName())
            .status(user.getStatus())
            .subCmdcv(user.getSubCmdcv())
            .build();
    }
}
