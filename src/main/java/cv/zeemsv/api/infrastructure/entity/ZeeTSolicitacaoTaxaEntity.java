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
@Table(name = "zee_t_solicitacao_taxa", schema = "public")
@Getter @Setter
public class ZeeTSolicitacaoTaxaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "id_solicitacao")
    private Integer idSolicitacao;

    @Column(name = "id_tp_solic_taxa", nullable = false)
    private Integer idTpSolicTaxa;

    @Column(name = "valor", nullable = false)
    private BigDecimal valor;

    @Column(name = "id_investidor")
    private Integer idInvestidor;

    @Column(name = "id_promotor")
    private Integer idPromotor;

    @Column(name = "id_projeto")
    private Integer idProjeto;

}
