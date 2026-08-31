package cv.zeemsv.api.application.audit.entity;

import java.time.LocalDateTime;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

@Getter
@Setter
@ToString
public class SessionAuditLog {
    @Id
    private ObjectId id;
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
