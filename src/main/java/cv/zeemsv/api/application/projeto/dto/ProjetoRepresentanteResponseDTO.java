package cv.zeemsv.api.application.projeto.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjetoRepresentanteResponseDTO {
    private Integer id;
    private Integer idProjeto;
    private Integer idRepresSocio;
    private Integer idRepresInvestidor;
    private String estado;
    private String estadoDesc;
    private LocalDate dateCreate;
    private BigDecimal userCreate;
    private Integer idInvestidor;
    private Integer idOrdem;
    private String dmTpRepresentante;
    private String dmTpRepresentanteDesc;
    private Boolean flagRepresentante;
    private Boolean flagSocio;
    private String dmPrincipal;
    private String dmPrincipalDesc;
    private String dmEstado;
    private String dmEstadoDesc;
    private LocalDate dataRegisto;
    private BigDecimal userRegisto;
    private Integer idUser;
    private String nome;
    private String nacionalidade;
    private String nacionalidadeId;
    private String nif;
    private String tipoDoc;
    private String tipoDocDesc;
    private String nrDoc;
    private BigDecimal telefone;
    private BigDecimal telemovel;
    private String email;
    private String fotoUrl;
    private String fotoPath;
    private String indicativoPais;
    private String endereco;
}
