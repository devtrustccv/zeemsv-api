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
public class PedidoAcessoInvestidorDetailResponseDTO {
    private PedidoAcessoInvestidorResponseDTO pedido;
    private UtilizadorDTO utilizador;
    private InvestidorDTO investidor;
    private SocioRepresentanteDTO socioRepresentante;
    private OrdemDTO ordem;

    @Getter
    @Setter
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class UtilizadorDTO {
        private Integer id;
        private String nome;
        private String email;
        private String dmEstado;
        private String dmEstadoDesc;
        private String origem;
        private String origemDesc;
        private Boolean onboardingRealizado;
        private LocalDate dataOnboarding;
        private LocalDate dataRegisto;
        private Integer pessoaId;
    }

    @Getter
    @Setter
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class InvestidorDTO {
        private Integer id;
        private String denominacao;
        private String matricula;
        private String dmNaturezaJuridica;
        private String dmNaturezaJuridicaDesc;
        private String setor;
        private String sede;
        private String dmClassificacao;
        private String dmClassificacaoDesc;
        private LocalDate dataConstituicao;
        private BigDecimal phone;
        private String indicativoPais;
        private BigDecimal telemovel;
        private String email;
        private String site;
        private String flagRec;
        private String dmEstado;
        private String dmEstadoDesc;
        private String linkRegComercial;
        private LocalDate dateCreate;
        private BigDecimal userCreate;
        private LocalDate dateUpdate;
        private BigDecimal userUpdate;
        private String formaObrigar;
        private BigDecimal capitalSocial;
        private String paisOrigem;
        private String endereco;
        private String nif;
        private String flagServico;
        private String dmIdioma;
        private String dmIdiomaDesc;
        private String dmTipoInvestidor;
        private String dmTipoInvestidorDesc;
        private String dmGenero;
        private String dmGeneroDesc;
        private LocalDate dataNascimento;
        private String dmEstadoCivil;
        private String dmEstadoCivilDesc;
        private String profissao;
        private String nrDocumento;
        private String moeda;
        private String moedaDesc;
    }

    @Getter
    @Setter
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class SocioRepresentanteDTO {
        private Integer id;
        private Integer idInvestidor;
        private String nome;
        private String nacionalidade;
        private String nif;
        private String tipoDoc;
        private String tipoDocDesc;
        private String nrDoc;
        private String dmTpRepresentante;
        private String dmTpRepresentanteDesc;
        private BigDecimal telefone;
        private BigDecimal telemovel;
        private String email;
        private String fotoUrl;
        private Boolean flagSocio;
        private Boolean flagRepresentante;
        private String dmPrincipal;
        private String dmPrincipalDesc;
        private String estado;
        private String estadoDesc;
        private LocalDate dateCreate;
        private BigDecimal userCreate;
        private String indicativoPais;
        private String endereco;
        private Integer idUser;
    }

    @Getter
    @Setter
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class OrdemDTO {
        private Integer id;
        private String tipoOrdem;
        private String tipoOrdemDesc;
        private String nome;
        private String cedula;
        private String concelho;
        private String endereco;
        private String email;
        private String indicativoPais;
        private BigDecimal telemovel;
        private BigDecimal nif;
        private String nrDocumento;
        private String nacionalidade;
        private BigDecimal numeroInscricao;
        private String especialidade;
        private String dmEstado;
        private String dmEstadoDesc;
        private LocalDate dataRegisto;
        private String userRegisto;
        private String dmTpDoc;
        private String dmTpDocDesc;
    }
}
