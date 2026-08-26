package cv.zeemsv.api.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "application.audit")
public class AuditProperties {
    private String defaultAppDad = "zeemsv";
    private String defaultAppName = "ZEEMSV";
}
