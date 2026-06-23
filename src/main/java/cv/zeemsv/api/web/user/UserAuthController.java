package cv.zeemsv.api.web.user;

import cv.zeemsv.api.application.user.dto.CredentialsLoginRequestDTO;
import cv.zeemsv.api.application.generic.dto.OtpResponseDto;
import cv.zeemsv.api.application.user.dto.ForgotPasswordRequestDTO;
import cv.zeemsv.api.application.user.dto.LoginResponseDTO;
import cv.zeemsv.api.application.user.dto.ResetPasswordRequestDTO;
import cv.zeemsv.api.application.user.dto.UserRegistrationOtpRequestDTO;
import cv.zeemsv.api.application.user.dto.UserRegistrationRequestDTO;
import cv.zeemsv.api.application.user.dto.UserRegistrationResponseDTO;
import cv.zeemsv.api.application.user.service.CredentialsLoginService;
import cv.zeemsv.api.application.user.service.PasswordRecoveryService;
import cv.zeemsv.api.application.user.service.UserRegistrationService;
import cv.zeemsv.api.interfaces.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class UserAuthController {
    private final UserRegistrationService userRegistrationService;
    private final CredentialsLoginService credentialsLoginService;
    private final PasswordRecoveryService passwordRecoveryService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserRegistrationResponseDTO>> register(
        @Valid @RequestBody UserRegistrationRequestDTO request
    ) {
        UserRegistrationResponseDTO response = userRegistrationService.register(request);
        return ResponseEntity.ok(ApiResponse.ok("Utilizador registado com sucesso", response));
    }

    @PostMapping("/register/otp/send")
    public ResponseEntity<ApiResponse<OtpResponseDto>> sendRegistrationOtp(
        @Valid @RequestBody UserRegistrationOtpRequestDTO request
    ) {
        OtpResponseDto response = userRegistrationService.sendRegistrationOtp(request);
        return ResponseEntity.ok(ApiResponse.ok("OTP de registo enviado com sucesso", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> login(
        @RequestHeader("X-FINGERPRINT") String fingerprint,
        @Valid @RequestBody CredentialsLoginRequestDTO request
    ) {
        LoginResponseDTO response = credentialsLoginService.login(request, fingerprint);
        return ResponseEntity.ok(ApiResponse.ok("Login efetuado com sucesso", response));
    }

    @PostMapping("/password/forgot")
    public ResponseEntity<ApiResponse<OtpResponseDto>> forgotPassword(
        @Valid @RequestBody ForgotPasswordRequestDTO request
    ) {
        OtpResponseDto response = passwordRecoveryService.forgotPassword(request);
        return ResponseEntity.ok(ApiResponse.ok("Se o email estiver registado, sera enviado um OTP de recuperacao.", response));
    }

    @PostMapping("/password/reset")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
        @Valid @RequestBody ResetPasswordRequestDTO request
    ) {
        passwordRecoveryService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.ok("Password atualizada com sucesso", null));
    }
}
