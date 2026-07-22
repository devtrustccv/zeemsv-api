package cv.zeemsv.api.application.solicitacao.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class SolicitacaoDocumentoRequestDTO {
    private Integer idTpSolicTpDoc;
    private String tipoDoc;
    private MultipartFile ficheiro;
}
