package cv.zeemsv.api.application.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionValidationResponseDTO {
    private boolean valid;
    private String message;
    private String accessToken;
}
