package cv.zeemsv.api.application.audit.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ChangeLogsItem {
    private String column;
    private Object oldValue;
    private Object newValue;
}
