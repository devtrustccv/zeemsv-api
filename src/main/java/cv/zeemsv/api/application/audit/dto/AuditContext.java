package cv.zeemsv.api.application.audit.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuditContext {
    private String userId;
    private String userEmail;
    private Integer profileId;
    private String profileName;
    private Integer orgId;
    private String orgName;
    private String appDad;
    private String appName;
}
