package cv.zeemsv.api.application.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
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
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponseDTO {
    private Integer userId;
    private String nome;
    private String email;
    private String subCmdcv;
    private String numDocumento;
    private String tipoDocumento;
    @Enumerated(EnumType.STRING)
    private UserStatus status;
    private Integer pessoaId;
    private Integer idSocioRepres;
    private String role;
    private String sessionToken;
}
