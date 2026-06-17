package cv.zeemsv.api.application.generic.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OtpResponseDto {
    private int expirationMinutes;
    private int otpLength;
    private LocalDateTime expiresAt;
}
