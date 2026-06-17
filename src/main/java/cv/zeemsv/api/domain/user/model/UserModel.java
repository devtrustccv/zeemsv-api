package cv.zeemsv.api.domain.user.model;

import cv.zeemsv.api.utils.enums.UserStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserModel {
    private Integer id;
    private Integer pessoaId;
    private String name;
    private String email;
    @Enumerated(EnumType.STRING)
    private UserStatus status;
    private String subCmdcv;
    private String provider;
}
