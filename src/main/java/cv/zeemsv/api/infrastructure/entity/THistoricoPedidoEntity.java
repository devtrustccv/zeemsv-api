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
@Table(name = "t_historico_pedido", schema = "public")
@Getter @Setter
public class THistoricoPedidoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "id_pedido", nullable = false)
    private Integer idPedido;

    @Column(name = "etapa_atual", nullable = false)
    private String etapaAtual;

    @Column(name = "cod_etapa_atual", nullable = false)
    private String codEtapaAtual;

    @Column(name = "id_etapa", nullable = false)
    private String idEtapa;

    @Column(name = "resultado")
    private String resultado;

    @Column(name = "motivo")
    private String motivo;

    @Column(name = "obs_despacho")
    private String obsDespacho;

    @Column(name = "dt_execucao", nullable = false)
    private LocalDate dtExecucao;

    @Column(name = "id_user_exec", nullable = false)
    private String idUserExec;

    @Column(name = "motivo_domain")
    private String motivoDomain;

}
