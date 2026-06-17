package cv.zeemsv.api.application.generic.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SessionTokenDto {
    private String sub;
    private String email;
    private String name;
    private String fingerprint;
    private boolean valid;
}
