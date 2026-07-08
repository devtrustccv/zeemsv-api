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
@Table(name = "zee_t_repres_investidor", schema = "public")
@Getter @Setter
public class ZeeTRepresInvestidorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "id_investidor", nullable = false)
    private Integer idInvestidor;

    @Column(name = "id_socio_repres")
    private Integer idSocioRepres;

    @Column(name = "id_ordem")
    private Integer idOrdem;

    @Column(name = "dm_tp_representante", nullable = false)
    private String dmTpRepresentante;

    @Column(name = "flag_representante")
    private Boolean flagRepresentante;

    @Column(name = "flag_socio")
    private Boolean flagSocio;

    @Column(name = "dm_principal")
    private String dmPrincipal;

    @Column(name = "dm_estado")
    private String dmEstado;

    @Column(name = "data_registo", nullable = false)
    private LocalDate dataRegisto;

    @Column(name = "user_registo", nullable = false)
    private BigDecimal userRegisto;

}
