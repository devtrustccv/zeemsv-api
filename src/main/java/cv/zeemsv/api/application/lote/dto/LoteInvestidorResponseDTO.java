package cv.zeemsv.api.application.lote.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LoteInvestidorResponseDTO {
    private Integer idLote;
    private String refLote;
    private String nip;
    private String dmSituacaoCd;
    private String dmSituacaoCdDesc;
    private String estado;
    private Integer idZona;
    private String zona;
    private BigDecimal area;
    private BigDecimal areaInicial;
    private Integer idInvestidor;
    private String condicao;
    private String origemAssociacao;
    private Integer idAssociacao;
    private Integer idProjeto;
    private String dmEstadoAssociacao;
    private String dmEstadoAssociacaoDesc;
    private LocalDate dataAssociacao;
    private String utilizadorAssociacao;
    private String dmEnquadramento;
    private String dmEnquadramentoDesc;
    private String projetoDenominacao;
    private String projetoDmRegime;
    private String projetoDmRegimeDesc;
    private String projetoDmProdutoServico;
    private String projetoDmProdutoServicoDesc;
    private String projetoDmEstadoProc;
    private String projetoDmEstadoProcDesc;
    private String projetoDmSituacao;
    private String projetoDmSituacaoDesc;
    private String projetoDmEstadoProj;
    private String projetoDmEstadoProjDesc;
    private LocalDate projetoDateCreate;
}
