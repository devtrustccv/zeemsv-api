package cv.zeemsv.api.application.user.dto;

import cv.zeemsv.api.application.pessoa.dto.ContatoResponseDTO;
import cv.zeemsv.api.utils.enums.UserStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.util.List;
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
public class UserAccountDetailResponseDTO {
    private Integer userId;
    @Enumerated(EnumType.STRING)
    private UserStatus status;
    private String sessionToken;
    private String email;
    private String subCmdcv;
    private String name;
    private String role;
    private Integer idSocioRepres;
    private String fotoSocioRepres;
    private List<ContatoResponseDTO> contacts;
}
