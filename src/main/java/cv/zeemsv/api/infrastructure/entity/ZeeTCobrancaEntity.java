package cv.zeemsv.api.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "zee_t_cobranca", schema = "public")
@Getter @Setter
public class ZeeTCobrancaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "nr_cobranca")
    private Integer nrCobranca;

    @Column(name = "id_solicitacao")
    private Integer idSolicitacao;

    @Column(name = "id_solic_taxa")
    private Integer idSolicTaxa;

    @Column(name = "id_investidor")
    private Integer idInvestidor;

    @Column(name = "id_projeto")
    private Integer idProjeto;

    @Column(name = "nr_processo")
    private Integer nrProcesso;

    @Column(name = "data_emissao")
    private LocalDate dataEmissao;

    @Column(name = "data_vencimento")
    private LocalDate dataVencimento;

    @Column(name = "valor_total")
    private String valorTotal;

    @Column(name = "valor_pago")
    private String valorPago;

    @Column(name = "valor_divida")
    private String valorDivida;

    @Column(name = "tipo_liquidacao")
    private String tipoLiquidacao;

    @Column(name = "nr_prestacao")
    private Integer nrPrestacao;

    @Column(name = "dm_estado")
    private String dmEstado;

    @Column(name = "user_registo")
    private String userRegisto;

    @Column(name = "data_registo")
    private LocalDate dataRegisto;
}
