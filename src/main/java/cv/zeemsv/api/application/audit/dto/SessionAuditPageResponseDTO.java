package cv.zeemsv.api.application.audit.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SessionAuditPageResponseDTO {
    private Long total;
    private Integer page;
    private Integer size;
    private List<SessionAuditResponseDTO> items;
}
