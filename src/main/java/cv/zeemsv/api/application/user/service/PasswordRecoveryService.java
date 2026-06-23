package cv.zeemsv.api.application.user.service;

import cv.zeemsv.api.application.generic.dto.OtpResponseDto;
import cv.zeemsv.api.application.generic.service.OTPService;
import cv.zeemsv.api.application.user.dto.ForgotPasswordRequestDTO;
import cv.zeemsv.api.application.user.dto.ResetPasswordRequestDTO;
import cv.zeemsv.api.domain.user.business.UserBus;
import cv.zeemsv.api.domain.user.model.UserModel;
import cv.zeemsv.api.exceptions.BusinessException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PasswordRecoveryService {
    private static final int OTP_LENGTH = 6;

    private final UserBus userBus;
    private final OTPService otpService;
    private final PasswordEncoder passwordEncoder;

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
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }
}
