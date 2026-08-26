package cv.zeemsv.api.application.user.service;

import cv.zeemsv.api.application.generic.dto.OtpResponseDto;
import cv.zeemsv.api.application.generic.service.OTPService;
import cv.zeemsv.api.application.audit.dto.AuditContext;
import cv.zeemsv.api.application.audit.entity.ChangeLogsItem;
import cv.zeemsv.api.application.audit.service.ChangeLogsService;
import cv.zeemsv.api.application.user.dto.ForgotPasswordRequestDTO;
import cv.zeemsv.api.application.user.dto.ResetPasswordRequestDTO;
import cv.zeemsv.api.domain.user.business.UserBus;
import cv.zeemsv.api.domain.user.model.UserModel;
import cv.zeemsv.api.exceptions.BusinessException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Log4j2
public class PasswordRecoveryService {
    private static final int OTP_LENGTH = 6;

    private final UserBus userBus;
    private final OTPService otpService;
    private final PasswordEncoder passwordEncoder;
    private final ChangeLogsService changeLogsService;

    @Value("${application.session.otp-expiration-in-minutes:5}")
    private int otpExpirationTime;

    @Transactional(readOnly = true)
    public OtpResponseDto forgotPassword(ForgotPasswordRequestDTO request) {
        String email = normalizeEmail(request.getEmail());
        userBus.findByEmail(email)
            .filter(user -> user.getPasswordHash() != null)
            .ifPresent(user -> otpService.sendOTP(email));

        return OtpResponseDto.builder()
            .otpLength(OTP_LENGTH)
            .expirationMinutes(otpExpirationTime)
            .expiresAt(LocalDateTime.now().plusMinutes(otpExpirationTime))
            .build();
    }

    @Transactional
    public void resetPassword(ResetPasswordRequestDTO request) {
        String email = normalizeEmail(request.getEmail());
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("Password e confirmacao de password nao coincidem.",
                new RuntimeException("Password e confirmacao de password nao coincidem."));
        }

        UserModel user = userBus.findByEmail(email)
            .filter(existingUser -> existingUser.getPasswordHash() != null)
            .orElseThrow(() -> new BusinessException("OTP invalido ou expirado.",
                new RuntimeException("OTP invalido ou expirado.")));

        if (!otpService.validateOtp(email, request.getOtp())) {
            throw new BusinessException("OTP invalido ou expirado.", new RuntimeException("OTP invalido ou expirado."));
        }

        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        userBus.save(user);
        auditPasswordReset(user);
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }

    private void auditPasswordReset(UserModel user) {
        try {
            ChangeLogsItem item = new ChangeLogsItem();
            item.setColumn("password_hash");
            item.setOldValue("***");
            item.setNewValue("***");
            changeLogsService.createLogsAsyncSafe(
                List.of(item),
                "UPDATE",
                "zee_t_user",
                String.valueOf(user.getId()),
                "Reset de password",
                AuditContext.builder()
                    .userId(user.getId() != null ? String.valueOf(user.getId()) : null)
                    .userEmail(user.getEmail())
                    .build()
            );
        } catch (RuntimeException ex) {
            log.warn("Nao foi possivel gravar auditoria do reset de password do utilizador {}.", user.getId(), ex);
        }
    }
}
