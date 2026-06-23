package cv.zeemsv.api.application.user.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserRegistrationRequestDTO {
    @NotBlank
    @Email
    private String email;

    private String name;

    @NotBlank
    @Size(min = 8)
    private String password;

    @NotBlank
    private String confirmPassword;

    @NotBlank
    private String otp;
}
