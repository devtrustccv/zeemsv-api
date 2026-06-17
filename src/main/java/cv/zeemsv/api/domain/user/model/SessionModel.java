package cv.zeemsv.api.domain.user.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionModel {
    private Integer id;
    private Integer userId;
    private String status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String sessionToken;
    private String provider;
}
