package cv.zeemsv.api.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "application.audit.mongodb")
public class AuditMongoProperties {
    private String url;
    private String databaseName = "zeemsvLogs";
    private AuditLogCollections collection = new AuditLogCollections();

    @Getter
    @Setter
    public static class AuditLogCollections {
        private String changes = "changeLogs";
        private String jobs = "jobLogs";
        private String app = "appLogs";
    }
}
