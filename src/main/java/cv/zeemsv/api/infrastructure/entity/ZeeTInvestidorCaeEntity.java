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
@Table(name = "zee_t_investidor_cae", schema = "public")
@Getter @Setter
public class ZeeTInvestidorCaeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "id_investidor", nullable = false)
    private Integer idInvestidor;

    @Column(name = "cae", length = 250)
    private String cae;

    @Column(name = "setor_cae", length = 4000)
    private String setorCae;

    @Column(name = "dm_principal", nullable = false, length = 4)
    private String dmPrincipal;

    @Column(name = "dm_estado", nullable = false, length = 20)
    private String dmEstado;

    @Column(name = "date_create", nullable = false)
    private LocalDate dateCreate;

    @Column(name = "user_create", nullable = false)
    private BigDecimal userCreate;

}
