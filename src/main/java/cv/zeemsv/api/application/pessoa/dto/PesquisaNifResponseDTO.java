package cv.zeemsv.api.application.pessoa.dto;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PesquisaNifResponseDTO {
    private String nif;
    private String nome;
    private String matricula;
    private String naturezaJuridica;
    private String setor;
    private String sede;
    private String classificacao;
    private String dataConstituicao;
    private BigDecimal telefone;
    private String indicativoPais;
    private BigDecimal telemovel;
    private String email;
    private String site;
    private String estado;
    private String linkRegComercial;
    private String formaObrigar;
    private BigDecimal capitalSocial;
    private String paisOrigem;
    private String endereco;
    private String flagServico;
    private String idioma;
    private String tipoInvestidor;
    private String genero;
    private String dataNascimento;
    private String estadoCivil;
    private String profissao;
    private String nrDocumento;
    private String moeda;
    private String origem;
}
