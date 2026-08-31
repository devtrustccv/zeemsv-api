package cv.zeemsv.api.application.audit.dto;

import java.time.LocalDateTime;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SessionAuditResponseDTO {
    private String id;
    private LocalDateTime date;
    private String userId;
    private String userName;
    private String userEmail;
    private String ip;
    private String authenticationMethod;
    private String state;
    private LocalDateTime startedAt;
    private LocalDateTime expiresAt;
    private LocalDateTime revokedAt;
    private String sessionId;
    private String userAgent;
    private Map<String, Object> metadata;
    private String appDad;
    private String appName;
}
