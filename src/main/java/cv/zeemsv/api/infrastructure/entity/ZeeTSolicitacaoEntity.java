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
@Table(name = "zee_t_solicitacao", schema = "public")
@Getter @Setter
public class ZeeTSolicitacaoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "id_tp_solicitacao", nullable = false)
    private Integer idTpSolicitacao;

    @Column(name = "id_pedido", nullable = false)
    private Integer idPedido;

    @Column(name = "id_entidade", nullable = false)
    private Integer idEntidade;

    @Column(name = "id_organica", nullable = false)
    private BigDecimal idOrganica;

    @Column(name = "id_processo", nullable = false)
    private BigDecimal idProcesso;

    @Column(name = "id_solic_pai")
    private Integer idSolicPai;

    @Column(name = "id_promotor")
    private Integer idPromotor;

    @Column(name = "id_investidor")
    private Integer idInvestidor;

    @Column(name = "id_projeto")
    private Integer idProjeto;

    @Column(name = "exposicao", length = 500)
    private String exposicao;

    @Column(name = "dm_origem", nullable = false, length = 20)
    private String dmOrigem;

    @Column(name = "user_solic", nullable = false)
    private String userSolic;

    @Column(name = "data_solic", nullable = false)
    private LocalDate dataSolic;

    @Column(name = "data_prev_resposta")
    private LocalDate dataPrevResposta;

    @Column(name = "desc_solic", nullable = false)
    private String descSolic;

    @Column(name = "dm_estado_proc", nullable = false)
    private String dmEstadoProc;

    @Column(name = "data_resposta")
    private LocalDate dataResposta;

    @Column(name = "user_resposta")
    private BigDecimal userResposta;

    @Column(name = "desc_resposta")
    private String descResposta;

    @Column(name = "prazo_dia")
    private BigDecimal prazoDia;

    @Column(name = "prazo_real")
    private BigDecimal prazoReal;

    @Column(name = "etapa_atual")
    private String etapaAtual;

    @Column(name = "id_ponto_focal_resp")
    private Integer idPontoFocalResp;

    @Column(name = "flag_correcao")
    private Boolean flagCorrecao;

    @Column(name = "data_envio_correcao")
    private LocalDate dataEnvioCorrecao;

    @Column(name = "data_fim_prevista_correcao")
    private LocalDate dataFimPrevistaCorrecao;

    @Column(name = "data_correcao")
    private LocalDate dataCorrecao;

    @Column(name = "user_corecao")
    private String userCorecao;

}
