package cv.zeemsv.api.application.investidor.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SocioRepresentanteResponseDTO {
    private Integer id;
    private Integer idInvestidor;
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
    private LocalDate dateCreate;
    private BigDecimal userCreate;
    private String indicativoPais;
    private Integer idUser;
}
