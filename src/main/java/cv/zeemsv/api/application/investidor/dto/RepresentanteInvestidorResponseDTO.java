package cv.zeemsv.api.application.investidor.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RepresentanteInvestidorResponseDTO {
    private Integer id;
    private Integer idInvestidor;
    private Integer idSocioRepres;
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
    private String indicativoPais;
}
