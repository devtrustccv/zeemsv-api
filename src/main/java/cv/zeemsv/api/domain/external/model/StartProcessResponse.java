package cv.zeemsv.api.domain.external.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StartProcessResponse {
    private String id;
    private String name;
    private Integer size;
    private Integer total;
    private String url;
    private OffsetDateTime createTime;
    private String executionId;
    private String executionUrl;
    private String formKey;
    private Integer priority;
    private String processDefinitionId;
    private String processDefinitionKey;
    private String processDefinitionUrl;
    private String processInstanceId;
    private Boolean suspended;
    private String taskDefinitionKey;
    private String tenantId;
    private String processName;

    @Builder.Default
    private List<Object> variables = new ArrayList<>();

    public static StartProcessResponse example() {
        return StartProcessResponse.builder().build();
    }

    public static StartProcessResponse minimal(String defKey, String defId, String instanceId) {
        return StartProcessResponse.builder()
            .processDefinitionKey(defKey)
            .processDefinitionId(defId)
            .processInstanceId(instanceId)
            .build();
    }

    public StartProcessResponse fillIfNulls() {
        if (id == null) {
            id = "";
        }
        if (name == null) {
            name = "";
        }
        if (size == null) {
            size = 0;
        }
        if (total == null) {
            total = 0;
        }
        if (url == null) {
            url = "";
        }
        if (createTime == null) {
            createTime = OffsetDateTime.now();
        }
        if (executionId == null) {
            executionId = "";
        }
        if (executionUrl == null) {
            executionUrl = "";
        }
        if (formKey == null) {
            formKey = "";
        }
        if (priority == null) {
            priority = 0;
        }
        if (processDefinitionId == null) {
            processDefinitionId = "";
        }
        if (processDefinitionKey == null) {
            processDefinitionKey = "";
        }
        if (processDefinitionUrl == null) {
            processDefinitionUrl = "";
        }
        if (processInstanceId == null) {
            processInstanceId = "";
        }
        if (suspended == null) {
            suspended = false;
        }
        if (taskDefinitionKey == null) {
            taskDefinitionKey = "";
        }
        if (tenantId == null) {
            tenantId = "";
        }
        if (variables == null) {
            variables = new ArrayList<>();
        }
        return this;
    }

    public StartProcessResponse addVar(String name, Object value) {
        if (variables == null) {
            variables = new ArrayList<>();
        }
        variables.add(Map.of("name", name, "value", value));
        return this;
    }
}
