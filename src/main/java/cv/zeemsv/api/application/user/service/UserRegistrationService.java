package cv.zeemsv.api.application.user.service;

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

    @Transactional
    public UserRegistrationResponseDTO register(UserRegistrationRequestDTO request) {
        String email = request.getEmail().trim().toLowerCase();
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("Password e confirmacao de password nao coincidem.",
                new RuntimeException("Password e confirmacao de password nao coincidem."));
        }

        if (userBus.findByEmail(email).isPresent()) {
            throw new BusinessException("Ja existe um utilizador registado com este email.",
                new RuntimeException("Ja existe um utilizador registado com este email."));
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
}
