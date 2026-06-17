package cv.zeemsv.api.domain.external.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CniResponseModel {
    @JsonProperty("NOME_COMPLETO")
    private String nome;

    @JsonProperty("DATA_NASC")
    private String dataNasc;

    @JsonProperty("NIF")
    private String nif;

    @JsonProperty("DT_EMISSAO")
    private String dtEmissao;

    @JsonProperty("DT_VALIDADE")
    private String dtValidade;

    @JsonProperty("NUM_DOCUMENTO")
    private String numDocumento;

    @JsonProperty("id_tp_doc")
    private String tipoDocumento;

    @JsonProperty("TELEMOVEL")
    private Long telefone;

    @JsonProperty("EMAIL")
    private String email;
}
