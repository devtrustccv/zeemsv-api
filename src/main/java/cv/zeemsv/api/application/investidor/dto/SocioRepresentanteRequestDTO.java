package cv.zeemsv.api.application.investidor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SocioRepresentanteRequestDTO {
    @NotNull(message = "O campo idInvestidor e obrigatorio")
    private Integer idInvestidor;

    @NotBlank(message = "O campo nome e obrigatorio")
    private String nome;

    private String nacionalidade;
    private String nif;
    private String tipoDoc;
    private String nrDoc;
    private String dmTpRepresentante;
    private BigDecimal telefone;
    private BigDecimal telemovel;
    private String email;
    private Boolean flagSocio;
    private Boolean flagRepresentante;
    private String dmPrincipal;
    private String estado;
    private BigDecimal userCreate;
    private String indicativoPais;
}
