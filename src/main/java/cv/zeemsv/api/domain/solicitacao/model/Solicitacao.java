package cv.zeemsv.api.domain.solicitacao.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Solicitacao {
    private Integer id;
    private String nome;
    private String descricao;
    private String estado;
    private Integer idTpSolicitacao;
    private Integer idPedido;
    private Integer idEntidade;
    private BigDecimal idOrganica;
    private BigDecimal idProcesso;
    private Integer idSolicPai;
    private Integer idPromotor;
    private Integer idInvestidor;
    private Integer idProjeto;
    private String exposicao;
    private String dmOrigem;
    private String userSolic;
    private LocalDate dataSolic;
    private LocalDate dataPrevResposta;
    private String descSolic;
    private String dmEstadoProc;
    private LocalDate dataResposta;
    private BigDecimal userResposta;
    private String descResposta;
    private BigDecimal prazoDia;
    private BigDecimal prazoReal;
    private String etapaAtual;
    private Integer idPontoFocalResp;
    private Boolean flagCorrecao;
    private LocalDate dataEnvioCorrecao;
    private LocalDate dataFimPrevistaCorrecao;
    private LocalDate dataCorrecao;
    private String userCorecao;
}
