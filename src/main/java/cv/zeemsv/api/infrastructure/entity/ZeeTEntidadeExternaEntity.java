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
@Table(name = "zee_t_entidade_externa", schema = "public")
@Getter @Setter
public class ZeeTEntidadeExternaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "id_aplicacao", nullable = false)
    private BigDecimal idAplicacao;

    @Column(name = "id_organica", nullable = false)
    private BigDecimal idOrganica;

    @Column(name = "denominacao", nullable = false)
    private String denominacao;

    @Column(name = "dm_tipo_ent", nullable = false)
    private String dmTipoEnt;

    @Column(name = "dm_tipo_ordem")
    private String dmTipoOrdem;

    @Column(name = "data_registo", nullable = false)
    private LocalDate dataRegisto;

    @Column(name = "user_registo", nullable = false)
    private BigDecimal userRegisto;

    @Column(name = "sigla")
    private String sigla;

}
