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
@Table(name = "zee_t_pedido_acesso_investidor", schema = "public")
@Getter
@Setter
public class ZeeTPedidoAcessoInvestidorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "id_utilizador", nullable = false)
    private Integer idUtilizador;

    @Column(name = "id_investidor", nullable = false)
    private Integer idInvestidor;

    @Column(name = "dm_tipo_pedido", nullable = false, length = 50)
    private String dmTipoPedido;

    @Column(name = "id_socio_repres")
    private Integer idSocioRepres;

    @Column(name = "nif_investidor", length = 20)
    private String nifInvestidor;

    @Column(name = "denominacao_entidade", length = 255)
    private String denominacaoEntidade;

    @Column(name = "dm_tp_representante", length = 50)
    private String dmTpRepresentante;

    @Column(name = "ficheiro_compravativo")
    private String ficheiroCompravativo;

    @Column(name = "dm_estado", length = 50)
    private String dmEstado;

    @Column(name = "obs", length = 1000)
    private String obs;

    @Column(name = "data_registo", nullable = false)
    private LocalDate dataRegisto;

    @Column(name = "data_resposta")
    private LocalDate dataResposta;

    @Column(name = "user_resposta", length = 100)
    private String userResposta;
}
