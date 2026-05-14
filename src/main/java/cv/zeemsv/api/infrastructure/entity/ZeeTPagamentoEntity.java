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
@Table(name = "zee_t_pagamento", schema = "public")
@Getter @Setter
public class ZeeTPagamentoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "id_solicitacao")
    private Integer idSolicitacao;

    @Column(name = "id_tp_solic_taxa")
    private Integer idTpSolicTaxa;

    @Column(name = "id_investidor")
    private Integer idInvestidor;

    @Column(name = "id_promotor")
    private Integer idPromotor;

    @Column(name = "id_projeto")
    private Integer idProjeto;

    @Column(name = "valor")
    private BigDecimal valor;

    @Column(name = "nr_processo")
    private String nrProcesso;

    @Column(name = "entidade")
    private String entidade;

    @Column(name = "referencia")
    private String referencia;

    @Column(name = "duc")
    private String duc;

    @Column(name = "dm_estado_pag")
    private String dmEstadoPag;

    @Column(name = "data_pagamento")
    private LocalDate dataPagamento;

    @Column(name = "data_registo")
    private LocalDate dataRegisto;

    @Column(name = "user_registo")
    private String userRegisto;

    @Column(name = "forma_pagamento")
    private String formaPagamento;

    @Column(name = "num_cheque")
    private String numCheque;

    @Column(name = "banco")
    private String banco;

    @Column(name = "link_duc")
    private String linkDuc;

    @Column(name = "id_solic_taxa")
    private Integer idSolicTaxa;

    @Column(name = "user_pagamento")
    private String userPagamento;

    @Column(name = "id_taxa")
    private Integer idTaxa;

}
