package cv.zeemsv.api.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "zee_t_tp_solic_repre", schema = "public")
@Getter @Setter
public class ZeeTTpSolicRepreEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "id_tp_solic", nullable = false)
    private Integer idTpSolic;

    @Column(name = "dm_tp_representante", nullable = false)
    private String dmTpRepresentante;

    @Column(name = "date_create", nullable = false)
    private LocalDate dateCreate;

    @Column(name = "user_create", nullable = false)
    private String userCreate;
}
