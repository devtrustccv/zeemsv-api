package cv.zeemsv.api.application.audit.dto;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccessAuditRequestDTO {
    private String userId;
    private String userName;
    private String userEmail;
    private String identifier;
    private String eventType;
    private String eventLabel;
    private String authenticationMethod;
    private String ip;
    private String userAgent;
    private String requestUri;
    private Integer statusCode;
    private Map<String, Object> metadata;
}
