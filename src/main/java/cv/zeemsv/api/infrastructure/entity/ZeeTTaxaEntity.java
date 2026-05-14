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
@Table(name = "zee_t_taxa", schema = "public")
@Getter @Setter
public class ZeeTTaxaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "codigo", nullable = false)
    private String codigo;

    @Column(name = "ref_fin", nullable = false)
    private String refFin;

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "valor")
    private BigDecimal valor;

    @Column(name = "tipo_taxa", nullable = false)
    private String tipoTaxa;

}
