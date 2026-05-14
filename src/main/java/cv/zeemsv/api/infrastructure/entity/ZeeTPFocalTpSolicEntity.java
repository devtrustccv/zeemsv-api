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
@Table(name = "zee_t_p_focal_tp_solic", schema = "public")
@Getter @Setter
public class ZeeTPFocalTpSolicEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "id_ponto_focal", nullable = false)
    private Integer idPontoFocal;

    @Column(name = "id_tp_ent_tp_solic", nullable = false)
    private Integer idTpEntTpSolic;

    @Column(name = "data_registo")
    private LocalDate dataRegisto;

    @Column(name = "dm_estado", length = 255)
    private String dmEstado;

    @Column(name = "user_registo")
    private Integer userRegisto;

}
