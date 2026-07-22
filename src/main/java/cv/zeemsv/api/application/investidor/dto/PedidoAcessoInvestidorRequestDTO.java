package cv.zeemsv.api.application.investidor.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PedidoAcessoInvestidorRequestDTO {
    @NotNull(message = "O campo id_user e obrigatorio")
    private Integer idUser;

    private Integer idInvestidor;

    private String tipoPedido;

    private Integer idSocioRepres;
    private Integer idOrdem;

    @JsonAlias("nif_entidsade")
    private String nifEntidade;
    private String denominacaoEntidade;
    @Email(message = "Email de contacto da entidade invalido")
    private String emailContactoEntidade;
    private BigDecimal telemovelContactoEntidade;
    private String dmTpRepresentante;
    private String nome;
    private String nacionalidade;
    private String nif;
    private String tipoDoc;
    private String nrDoc;
    private BigDecimal telefone;
    private BigDecimal telemovel;

    @Email(message = "Email invalido")
    private String email;

    private String fotoUrl;
    private String indicativoPais;
    private String endereco;
    private String obs;
}
