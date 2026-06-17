package cv.zeemsv.api.domain.external.model;

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
public class AuthenticatedUserInfo {
    private String sub;
    private String email;
    private String name;
    private String username;
    private String cmdStatus;

    public boolean isCmdActive() {
        return "ACTIVE".equalsIgnoreCase(cmdStatus);
    }
}
