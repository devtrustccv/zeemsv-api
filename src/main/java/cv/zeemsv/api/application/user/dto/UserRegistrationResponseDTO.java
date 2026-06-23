package cv.zeemsv.api.application.user.dto;

import cv.zeemsv.api.utils.enums.UserStatus;
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
public class UserRegistrationResponseDTO {
    private Integer userId;
    private String email;
    private String name;
    private UserStatus status;
}
