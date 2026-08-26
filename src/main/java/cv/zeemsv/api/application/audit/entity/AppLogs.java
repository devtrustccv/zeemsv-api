package cv.zeemsv.api.application.audit.entity;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

@Getter
@Setter
@ToString
public class AppLogs {
    @Id
    private ObjectId id;
    private LocalDateTime date;
    private String level;
    private String message;
    private String exceptionClass;
    private String stackTrace;
    private Integer status;
    private String method;
    private String path;
    private String queryString;
    private String remoteAddr;
    private String userAgent;
}
