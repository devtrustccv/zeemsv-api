package cv.zeemsv.api.domain.investidor.model;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class InvestidorUser {
    private Integer id;
    private String denominacao;
    private String nif;
    private String email;
    private BigDecimal telemovel;
    private String dmEstado;
    private String dmTipoInvestidor;
    private String paisOrigem;
    private String endereco;
    private String dmTpRepresentante;
    private String dmPrincipal;
}
