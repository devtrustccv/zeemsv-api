package cv.zeemsv.api.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "zee_t_proj_lote_enquad", schema = "public")
@Getter @Setter
public class ZeeTProjLoteEnquadEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "id_projeto", nullable = false)
    private Integer idProjeto;

    @Column(name = "id_lote_enquadr", nullable = false)
    private Integer idLoteEnquadr;

    @Column(name = "user_create", nullable = false)
    private BigDecimal userCreate;

    @Column(name = "data_create", nullable = false)
    private LocalDate dataCreate;

    @Column(name = "dm_estado", nullable = false, length = 20)
    private String dmEstado;

    @Column(name = "id_proj_lote", nullable = false)
    private Integer idProjLote;

}
