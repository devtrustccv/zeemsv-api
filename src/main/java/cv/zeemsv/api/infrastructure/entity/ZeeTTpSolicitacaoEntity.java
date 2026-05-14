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
@Table(name = "zee_t_tp_solicitacao", schema = "public")
@Getter @Setter
public class ZeeTTpSolicitacaoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "nome")
    private String nome;

    @Column(name = "dm_tipo_solicitacao")
    private String dmTipoSolicitacao;

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "msg_pedido")
    private String msgPedido;

    @Column(name = "prazo_dia")
    private BigDecimal prazoDia;

    @Column(name = "flag_obrigatorio")
    private String flagObrigatorio;

    @Column(name = "codigo", length = 255)
    private String codigo;

    @Column(name = "dm_estado", length = 255)
    private String dmEstado;

    @Column(name = "id_ent_externa")
    private Integer idEntExterna;

    @Column(name = "possui_taxa")
    private String possuiTaxa;

}
