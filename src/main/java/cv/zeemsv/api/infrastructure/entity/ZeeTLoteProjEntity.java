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
@Table(name = "zee_t_lote_proj", schema = "public")
@Getter @Setter
public class ZeeTLoteProjEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "id_proj", nullable = false)
    private Integer idProj;

    @Column(name = "id_lote", nullable = false)
    private Integer idLote;

    @Column(name = "dm_estado", nullable = false, length = 4)
    private String dmEstado;

    @Column(name = "user_create", nullable = false)
    private BigDecimal userCreate;

    @Column(name = "date_create", nullable = false)
    private LocalDate dateCreate;

    @Column(name = "data_desa")
    private LocalDate dataDesa;

    @Column(name = "user_desa")
    private String userDesa;

}
