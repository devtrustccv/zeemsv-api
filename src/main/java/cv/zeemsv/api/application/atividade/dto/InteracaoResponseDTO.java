package cv.zeemsv.api.application.atividade.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class InteracaoResponseDTO {
    private Integer id;
    private Integer idInvestidor;
    private Integer idUser;
    private String nome;
    private String email;
    private String telefone;
    private String tipoInteracao;
    private String tipoInteracaoDesc;
    private String dmEstadoInteracao;
    private String dmEstadoInteracaoDesc;
    private String assuntoInteracao;
    private String mensagemInteracao;
    private Integer userResposta;
    private LocalDate dataResposta;
    private String mensagemResposta;
    private LocalDate dataCreate;
    private List<InteracaoAnexoResponseDTO> anexos;
    private List<InteracaoMensagemResponseDTO> mensagensResposta;
}
