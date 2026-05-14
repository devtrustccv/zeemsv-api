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
@Table(name = "t_pedido", schema = "public")
@Getter @Setter
public class TPedidoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "dm_estado_pedido", length = 255)
    private String dmEstadoPedido;

    @Column(name = "dm_origem_reg", length = 255)
    private String dmOrigemReg;

    @Column(name = "dt_despacho")
    private LocalDate dtDespacho;

    @Column(name = "dt_fim")
    private LocalDate dtFim;

    @Column(name = "dt_registo")
    private LocalDate dtRegisto;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "etapa_atual", length = 255)
    private String etapaAtual;

    @Column(name = "id_etapa", length = 255)
    private String idEtapa;

    @Column(name = "id_organica")
    private BigDecimal idOrganica;

    @Column(name = "id_processo")
    private BigDecimal idProcesso;

    @Column(name = "id_tp_processo", length = 255)
    private String idTpProcesso;

    @Column(name = "id_user_despacho")
    private BigDecimal idUserDespacho;

    @Column(name = "id_user_reg")
    private BigDecimal idUserReg;

    @Column(name = "obs_despacho", length = 4000)
    private String obsDespacho;

    @Column(name = "resultado", length = 255)
    private String resultado;

    @Column(name = "tipo_relacao")
    private String tipoRelacao;

    @Column(name = "id_relacao")
    private BigDecimal idRelacao;

    @Column(name = "cod_etapa_atual")
    private String codEtapaAtual;

    @Column(name = "id_etapa_atual")
    private String idEtapaAtual;

    @Column(name = "motivo", length = 255)
    private String motivo;

    @Column(name = "motivo_domain", length = 255)
    private String motivoDomain;

    @Column(name = "requerente")
    private String requerente;

    @Column(name = "tp_processo")
    private String tpProcesso;

}
