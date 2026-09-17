package cv.zeemsv.api.application.projeto.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjetoDocumentoResponseDTO {
    private Integer id;
    private String tipoRelacao;
    private String tipoRelacaoDesc;
    private BigDecimal idRelacao;
    private String objetoDescricao;
    private String idDoc;
    private Integer idTpDoc;
    private String nomeDocumento;
    private String estado;
    private String estadoDesc;
    private LocalDateTime dateCreate;
    private String userCreate;
    private String path;
    private String url;
    private String nomeFicheiro;
    private BigDecimal docSize;
    private String mimetype;
    private String descricao;
}
