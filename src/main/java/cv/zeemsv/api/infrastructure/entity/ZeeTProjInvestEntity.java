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
@Table(name = "zee_t_proj_invest", schema = "public")
@Getter @Setter
public class ZeeTProjInvestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "denominacao", length = 500)
    private String denominacao;

    @Column(name = "dm_enquadrameno", length = 250)
    private String dmEnquadrameno;

    @Column(name = "dm_regime", length = 250)
    private String dmRegime;

    @Column(name = "dm_produto_servico", length = 250)
    private String dmProdutoServico;

    @Column(name = "dm_estado_install", length = 250)
    private String dmEstadoInstall;

    @Column(name = "dm_estado_proc", length = 250)
    private String dmEstadoProc;

    @Column(name = "estado", nullable = false, length = 20)
    private String estado;

    @Column(name = "date_create", nullable = false)
    private LocalDate dateCreate;

    @Column(name = "user_create", nullable = false)
    private BigDecimal userCreate;

    @Column(name = "dm_doc_falta")
    private Boolean dmDocFalta;

    @Column(name = "id_investidor_cae", nullable = false)
    private Integer idInvestidorCae;

    @Column(name = "dm_situacao", length = 50)
    private String dmSituacao;

    @Column(name = "id_investidor", nullable = false)
    private Integer idInvestidor;

    @Column(name = "dm_estado_proj", length = 50)
    private String dmEstadoProj;

    @Column(name = "data_desistencia")
    private LocalDate dataDesistencia;

    @Column(name = "user_desistencia")
    private BigDecimal userDesistencia;

    @Column(name = "motivo", length = 4000)
    private String motivo;

}
