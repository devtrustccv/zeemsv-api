package cv.zeemsv.api.application.solicitacao.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SolicitacaoResponseDTO {
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
    private String dmEstadoProcDesc;
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

    private String tpSolicitacaoNome;
    private String tpSolicitacaoDescricao;
    private String tpSolicitacaoCodigo;
    private String tpSolicitacaoDmTipo;
    private String tpSolicitacaoDmTipoDesc;
    private String tpSolicitacaoDmEstado;
    private String tpSolicitacaoDmEstadoDesc;

    private String investidorDenominacao;
    private String investidorNif;
    private String investidorEmail;
    private BigDecimal investidorTelemovel;
    private String investidorPaisOrigem;

    private String promotorDenominacao;
    private String promotorNif;
    private String promotorEmail;
    private BigDecimal promotorTelemovel;
    private String promotorPaisOrigem;

    private String projetoDenominacao;
    private String projetoDmRegime;
    private String projetoDmRegimeDesc;
    private String projetoDmProdutoServico;
    private String projetoDmProdutoServicoDesc;
    private String projetoDmEstadoProc;
    private String projetoDmEstadoProcDesc;
    private String projetoDmSituacao;
    private String projetoDmSituacaoDesc;

    private String pedidoDmEstadoPedido;
    private String pedidoDmEstadoPedidoDesc;
    private String pedidoEtapaAtual;
    private String pedidoCodEtapaAtual;
    private LocalDate pedidoDtRegisto;
    private LocalDate pedidoDtDespacho;
    private LocalDate pedidoDtFim;
    private String pedidoObsDespacho;
    private String pedidoResultado;
    private String pedidoRequerente;
}
