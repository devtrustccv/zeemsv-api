package cv.zeemsv.api.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "zee_t_tp_ent_tp_solic", schema = "public")
@Getter @Setter
public class ZeeTTpEntTpSolicEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "id_ent_externa", nullable = false)
    private Integer idEntExterna;

    @Column(name = "id_tp_solic", nullable = false)
    private Integer idTpSolic;

    @Column(name = "dm_estado", nullable = false)
    private String dmEstado;

    @Column(name = "prazo_dia", nullable = false)
    private BigDecimal prazoDia;

    @Column(name = "flag_obrigatorio")
    private String flagObrigatorio;

}
