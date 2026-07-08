package cv.zeemsv.api.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "t_notificacao", schema = "gestao_notificacao")
@Getter @Setter
public class TNotificacaoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "id_pai")
    private Integer idPai;

    @Column(name = "id_aplicacao", nullable = false)
    private BigDecimal idAplicacao;

    @Column(name = "id_organica", nullable = false)
    private BigDecimal idOrganica;

    @Column(name = "user_registo", nullable = false)
    private BigDecimal userRegisto;

    @Column(name = "data_registo", nullable = false)
    private LocalDate dataRegisto;

    @Column(name = "assunto")
    private String assunto;

    @Column(name = "data_envio")
    private LocalDateTime dataEnvio;

    @Column(name = "mensagem", nullable = false, length = 4000)
    private String mensagem;

    @Column(name = "mensagem_confirmacao", length = 4000)
    private String mensagemConfirmacao;

    @Column(name = "email", length = 500)
    private String email;

    @Column(name = "telemovel")
    private String telemovel;

    @Column(name = "estado")
    private String estado;

    @Column(name = "flag_automatico")
    private String flagAutomatico;

    @Column(name = "flag_sucesso")
    private String flagSucesso;

    @Column(name = "flag_leitura")
    private String flagLeitura;

    @Column(name = "user_leitura")
    private BigDecimal userLeitura;

    @Column(name = "data_leitura")
    private LocalDateTime dataLeitura;

    @Column(name = "numero_reenvios")
    private BigDecimal numeroReenvios;

    @Column(name = "tipo")
    private String tipo;

    @Column(name = "id_relacao")
    private Integer idRelacao;

    @Column(name = "de")
    private String de;

    @Column(name = "emails_enviados")
    private String emailsEnviados;

    @Column(name = "confirm_recebimento")
    private Boolean confirmRecebimento;
}
