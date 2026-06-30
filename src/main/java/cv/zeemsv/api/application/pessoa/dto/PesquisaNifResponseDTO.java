package cv.zeemsv.api.application.pessoa.dto;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PesquisaNifResponseDTO {
    private Integer idInvestidor;
    private String nif;
    private String nome;
    private String matricula;
    private String naturezaJuridica;
    private String naturezaJuridicaDesc;
    private String setor;
    private String sede;
    private String classificacao;
    private String classificacaoDesc;
    private String dataConstituicao;
    private BigDecimal telefone;
    private String indicativoPais;
    private BigDecimal telemovel;
    private String email;
    private String site;
    private String estado;
    private String estadoDesc;
    private String linkRegComercial;
    private String formaObrigar;
    private BigDecimal capitalSocial;
    private String paisOrigem;
    private String endereco;
    private String flagServico;
    private String flagServicoDesc;
    private String idioma;
    private String idiomaDesc;
    private String tipoInvestidor;
    private String tipoInvestidorDesc;
    private String genero;
    private String generoDesc;
    private String dataNascimento;
    private String estadoCivil;
    private String estadoCivilDesc;
    private String profissao;
    private String nrDocumento;
    private String moeda;
    private String moedaDesc;
    private String origem;
}
