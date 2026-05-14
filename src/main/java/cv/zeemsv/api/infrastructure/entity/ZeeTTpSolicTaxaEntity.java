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
@Table(name = "zee_t_tp_solic_taxa", schema = "public")
@Getter @Setter
public class ZeeTTpSolicTaxaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "id_tp_solic", nullable = false)
    private Integer idTpSolic;

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "valor")
    private BigDecimal valor;

    @Column(name = "tipo_taxa", nullable = false)
    private String tipoTaxa;

    @Column(name = "id_taxa", nullable = false)
    private Integer idTaxa;

    @Column(name = "id_investidor")
    private Integer idInvestidor;

    @Column(name = "id_promotor")
    private Integer idPromotor;

    @Column(name = "id_projeto")
    private Integer idProjeto;

}
