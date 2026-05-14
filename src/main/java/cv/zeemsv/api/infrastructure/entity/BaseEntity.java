package cv.zeemsv.api.infrastructure.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@MappedSuperclass
@Getter @Setter
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "data_registo")
    private LocalDateTime dataRegisto;

    @Column(name = "data_update")
    private LocalDateTime dataUpdate;

    @Column(name = "user_registo")
    private Long userRegisto;

    @Column(name = "user_update")
    private Long userUpdate;
}
