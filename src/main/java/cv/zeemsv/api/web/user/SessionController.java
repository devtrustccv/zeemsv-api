package cv.zeemsv.api.web.user;

import cv.zeemsv.api.application.generic.dto.OtpResponseDto;
import cv.zeemsv.api.application.user.dto.LoginRequestDTO;
import cv.zeemsv.api.application.user.dto.LoginResponseDTO;
import cv.zeemsv.api.application.user.dto.SessionValidationResponseDTO;
import cv.zeemsv.api.application.user.dto.UserAccountDetailResponseDTO;
import cv.zeemsv.api.application.user.service.SessionService;
import cv.zeemsv.api.interfaces.dto.ApiResponse;
import cv.zeemsv.api.utils.enums.LoginProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/session")
@RequiredArgsConstructor
public class SessionController {
    private final SessionService sessionService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> login(
        @RequestHeader("X-OPENID-TOKEN") String accessToken,
        @RequestHeader("X-FINGERPRINT") String fingerprint,
        @RequestParam(required = false, name = "url_redirect") String urlRedirect,
        @RequestParam(required = false, name = "provider", defaultValue = "AUTENTIKA") LoginProvider provider
    ) {
        LoginRequestDTO request = LoginRequestDTO.builder()
            .autentikaToken(accessToken)
            .fingerprint(fingerprint)
            .urlRedirect(urlRedirect)
            .loginProvider(provider)
            .build();

        LoginResponseDTO response = sessionService.login(request);
        return ResponseEntity.ok(ApiResponse.ok("Sessao criada com sucesso", response));
    }

    @GetMapping("/validate")
    public ResponseEntity<ApiResponse<SessionValidationResponseDTO>> validateSession(
        @RequestHeader(name = "X-SESSION-TOKEN") String accessToken,
        @RequestHeader(name = "X-FINGERPRINT") String fingerprint
    ) {
        SessionValidationResponseDTO response = sessionService.validateSession(accessToken, fingerprint);
        if (!response.isValid()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.fail(response.getMessage(), response));
        }
        return ResponseEntity.ok(ApiResponse.ok(response.getMessage(), response));
    }

    @GetMapping("/logout")
    public ResponseEntity<ApiResponse<SessionValidationResponseDTO>> logout(
        @RequestHeader(name = "X-SESSION-TOKEN") String accessToken,
        @RequestHeader(name = "X-FINGERPRINT") String fingerprint
    ) {
        SessionValidationResponseDTO response = sessionService.logout(accessToken, fingerprint);
        if (!response.isValid()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(response.getMessage(), response));
        }
        return ResponseEntity.ok(ApiResponse.ok(response.getMessage(), response));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserAccountDetailResponseDTO>> userAccountDetail(
        @RequestHeader(name = "X-SESSION-TOKEN") String accessToken,
        @RequestHeader(name = "X-FINGERPRINT") String fingerprint,
        @RequestParam(required = false, name = "load_contacts", defaultValue = "false") boolean loadContacts,
        @RequestParam(required = false, name = "load_info_school", defaultValue = "false") boolean loadInfoSchool
    ) {
        UserAccountDetailResponseDTO response = sessionService.userAccountDetailByAccessToken(
            accessToken,
            fingerprint,
            loadContacts,
            loadInfoSchool
        );
        return ResponseEntity.ok(ApiResponse.ok("Detalhes da conta encontrados", response));
    }

    @GetMapping("/otp/send")
    public ResponseEntity<ApiResponse<OtpResponseDto>> sendOtpUsingSession(
        @RequestHeader(name = "X-SESSION-TOKEN") String accessToken,
        @RequestHeader(name = "X-FINGERPRINT") String fingerprint,
        @RequestParam(required = false, name = "email") String email
    ) {
        OtpResponseDto response = sessionService.sendOtpUsingSession(accessToken, fingerprint, email);
        return ResponseEntity.ok(ApiResponse.ok("OTP enviado com sucesso", response));
    }

    @GetMapping("/otp/validate")
    public ResponseEntity<ApiResponse<Void>> validateOtpUsingSession(
        @RequestHeader(name = "X-SESSION-TOKEN") String accessToken,
        @RequestHeader(name = "X-FINGERPRINT") String fingerprint,
        @RequestParam(name = "otp") String otp
    ) {
        boolean valid = sessionService.validateOtpUsingSession(accessToken, fingerprint, otp);
        if (!valid) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail("OTP invalido", null));
        }
        return ResponseEntity.ok(ApiResponse.ok("OTP validado com sucesso", null));
    }
}
