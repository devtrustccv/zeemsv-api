package cv.zeemsv.api.application.audit.entity;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

@Getter
@Setter
@ToString
public class ChangeLogs {
    @Id
    private ObjectId id;
    private LocalDateTime date;
    private String action;
    private String tableName;
    private String tableId;
    private List<ChangeLogsItem> logsItems;
    private String obs;
    private String userId;
    private String userEmail;
    private Integer profileId;
    private String profileName;
    private Integer orgId;
    private String orgName;
    private String appDad;
    private String appName;
}
