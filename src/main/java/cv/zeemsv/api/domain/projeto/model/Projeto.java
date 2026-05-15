package cv.zeemsv.api.domain.projeto.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Projeto {
    private Integer id;
    private String nome;
    private String descricao;
    private String estado;
    private String denominacao;
    private String dmEnquadrameno;
    private String dmRegime;
    private String dmProdutoServico;
    private String dmEstadoInstall;
    private String dmEstadoProc;
    private LocalDate dateCreate;
    private BigDecimal userCreate;
    private Boolean dmDocFalta;
    private Integer idInvestidorCae;
    private String dmSituacao;
    private Integer idInvestidor;
    private String dmEstadoProj;
    private LocalDate dataDesistencia;
    private BigDecimal userDesistencia;
    private String motivo;
}
