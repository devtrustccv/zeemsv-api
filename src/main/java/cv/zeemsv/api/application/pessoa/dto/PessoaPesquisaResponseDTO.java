package cv.zeemsv.api.application.pessoa.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PessoaPesquisaResponseDTO {
    private String nome;
    private String nif;
    private String nrDocumento;
    private String tipoDocumento;
    private String tpDoc;
    private String dataNascimento;
    private String dataEmissao;
    private String dataValidade;
    private String nacionalidade;
    private String nacionalidadeId;
    private String telemovel;
    private String estadoCivil;
    private String genero;
    private String email;
    private String residencia;
    private String nomeMae;
    private String nomePai;
    private String emissor;
    private String origem;
}
