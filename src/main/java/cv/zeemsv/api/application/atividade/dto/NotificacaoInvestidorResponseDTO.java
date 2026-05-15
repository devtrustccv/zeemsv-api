package cv.zeemsv.api.application.atividade.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class NotificacaoInvestidorResponseDTO {
    private Integer idRelacaoNotificacao;
    private String tpRelacao;
    private BigDecimal idRelacao;
    private Integer idNotificacao;
    private Integer idPai;
    private BigDecimal idAplicacao;
    private BigDecimal idOrganica;
    private BigDecimal userRegisto;
    private LocalDate dataRegisto;
    private String assunto;
    private LocalDateTime dataEnvio;
    private String mensagem;
    private String mensagemConfirmacao;
    private String email;
    private String telemovel;
    private String estado;
    private String flagAutomatico;
    private String flagSucesso;
    private String flagLeitura;
    private BigDecimal userLeitura;
    private BigDecimal numeroReenvios;
    private String tipo;
    private Integer notificacaoIdRelacao;
    private String de;
    private String emailsEnviados;
    private Boolean confirmRecebimento;
    private Long totalAnexos;
    private Map<String, Object> relacao;
}
