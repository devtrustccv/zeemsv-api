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
@Table(name = "zee_t_projeto_repres", schema = "public")
@Getter @Setter
public class ZeeTProjetoRepresEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "id_projeto", nullable = false)
    private Integer idProjeto;

    @Column(name = "id_repres_socio")
    private Integer idRepresSocio;

    @Column(name = "estado", nullable = false, length = 20)
    private String estado;

    @Column(name = "date_create", nullable = false)
    private LocalDate dateCreate;

    @Column(name = "user_create", nullable = false)
    private BigDecimal userCreate;

    @Column(name = "id_repres_investidor")
    private Integer idRepresInvestidor;

}
