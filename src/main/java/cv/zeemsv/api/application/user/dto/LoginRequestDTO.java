package cv.zeemsv.api.application.user.dto;

import cv.zeemsv.api.utils.enums.LoginProvider;
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
public class LoginRequestDTO {
    private String autentikaToken;
    private String fingerprint;
    private String urlRedirect;
    private LoginProvider loginProvider;
}
