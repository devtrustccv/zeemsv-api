package cv.zeemsv.api.domain.generic.model;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class OtpModel {
    private final String email;
    private final String otpHash;
    private final int expirationMinutes;
    private final int otpLength;
    private final LocalDateTime expiresAt;

    public OtpModel(String email, String otpHash, int expirationMinutes, int otpLength) {
        this.email = email;
        this.otpHash = otpHash;
        this.expirationMinutes = expirationMinutes;
        this.otpLength = otpLength;
        this.expiresAt = LocalDateTime.now().plusMinutes(expirationMinutes);
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
}
