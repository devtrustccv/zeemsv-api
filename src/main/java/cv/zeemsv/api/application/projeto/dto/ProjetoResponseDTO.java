package cv.zeemsv.api.application.projeto.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ProjetoResponseDTO {
    private Integer id;
    private String nome;
    private String descricao;
    private String estado;
    private String denominacao;
    private String dmEnquadrameno;
    private String dmEnquadramenoDesc;
    private String dmRegime;
    private String dmRegimeDesc;
    private String dmProdutoServico;
    private String dmProdutoServicoDesc;
    private String dmEstadoInstall;
    private String dmEstadoInstallDesc;
    private String dmEstadoProc;
    private String dmEstadoProcDesc;
    private LocalDate dateCreate;
    private BigDecimal userCreate;
    private Boolean dmDocFalta;
    private String dmDocFaltaDesc;
    private Integer idInvestidorCae;
    private String atividadePrincipal;
    private String atividadePrincipalSetor;
    private String dmSituacao;
    private String dmSituacaoDesc;
    private Integer idInvestidor;
    private String dmEstadoProj;
    private String dmEstadoProjDesc;
    private LocalDate dataDesistencia;
    private BigDecimal userDesistencia;
    private String motivo;
}
