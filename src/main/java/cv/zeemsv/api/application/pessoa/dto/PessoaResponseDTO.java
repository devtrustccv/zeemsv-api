package cv.zeemsv.api.application.pessoa.dto;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PessoaResponseDTO {
    private Integer id;
    private String nome;
    private String email;
    private String numDocumento;
    private String tipoDocumento;
    private LocalDate dataNasc;
    private String telefone;
}
