package cv.zeemsv.api.application.audit.dto;

import java.time.LocalDateTime;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionAuditResponseDTO {
    private String id;
    private LocalDateTime date;
    private String userId;
    private String userName;
    private String userEmail;
    private String actionType;
    private String actionLabel;
    private String description;
    private String tableName;
    private String tableId;
    private String module;
    private String ip;
    private String userAgent;
    private String requestMethod;
    private String requestUri;
    private Integer statusCode;
    private Map<String, Object> metadata;
    private String appDad;
    private String appName;
}
