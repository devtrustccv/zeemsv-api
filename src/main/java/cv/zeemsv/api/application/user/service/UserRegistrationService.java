package cv.zeemsv.api.application.user.service;

import cv.zeemsv.api.application.generic.dto.OtpResponseDto;
import cv.zeemsv.api.application.generic.service.OTPService;
import cv.zeemsv.api.application.user.dto.UserRegistrationOtpRequestDTO;
import cv.zeemsv.api.application.user.dto.UserRegistrationRequestDTO;
import cv.zeemsv.api.application.user.dto.UserRegistrationResponseDTO;
import cv.zeemsv.api.domain.user.business.UserBus;
import cv.zeemsv.api.domain.user.model.UserModel;
import cv.zeemsv.api.exceptions.BusinessException;
import cv.zeemsv.api.utils.enums.LoginProvider;
import cv.zeemsv.api.utils.enums.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserRegistrationService {
    private final UserBus userBus;
    private final PasswordEncoder passwordEncoder;
    private final OTPService otpService;

    public OtpResponseDto sendRegistrationOtp(UserRegistrationOtpRequestDTO request) {
        String email = normalizeEmail(request.getEmail());
        if (userBus.findByEmail(email).isPresent()) {
            throw new BusinessException("Ja existe um utilizador registado com este email.",
                new RuntimeException("Ja existe um utilizador registado com este email."));
        }

        var otp = otpService.sendOTP(email);
        return OtpResponseDto.builder()
            .otpLength(otp.getOtpLength())
            .expirationMinutes(otp.getExpirationMinutes())
            .expiresAt(otp.getExpiresAt())
            .build();
    }

    @Transactional
    public UserRegistrationResponseDTO register(UserRegistrationRequestDTO request) {
        String email = normalizeEmail(request.getEmail());
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("Password e confirmacao de password nao coincidem.",
                new RuntimeException("Password e confirmacao de password nao coincidem."));
        }

        if (userBus.findByEmail(email).isPresent()) {
            throw new BusinessException("Ja existe um utilizador registado com este email.",
                new RuntimeException("Ja existe um utilizador registado com este email."));
        }

        if (!otpService.validateOtp(email, request.getOtp())) {
            throw new BusinessException("OTP invalido ou expirado.", new RuntimeException("OTP invalido ou expirado."));
        }

        UserModel user = UserModel.builder()
            .email(email)
            .name(request.getName())
            .passwordHash(passwordEncoder.encode(request.getPassword()))
            .status(UserStatus.PENDENTE)
            .provider(LoginProvider.LOCAL.name())
            .build();

        UserModel saved = userBus.save(user);
        return UserRegistrationResponseDTO.builder()
            .userId(saved.getId())
            .email(saved.getEmail())
            .name(saved.getName())
            .status(saved.getStatus())
            .build();
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }
}
