package cv.zeemsv.api.application.investidor.dto;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class InvestidorUserResponseDTO {
    private Integer id;
    private String denominacao;
    private String nif;
    private String email;
    private BigDecimal telemovel;
    private String dmEstado;
    private String dmEstadoDesc;
    private String dmTipoInvestidor;
    private String dmTipoInvestidorDesc;
    private String paisOrigem;
    private String endereco;
    private String dmTpRepresentante;
    private String dmTpRepresentanteDesc;
    private String dmPrincipal;
    private String dmPrincipalDesc;
}
