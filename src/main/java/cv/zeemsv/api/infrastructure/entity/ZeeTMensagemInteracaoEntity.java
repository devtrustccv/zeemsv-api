package cv.zeemsv.api.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "zee_t_mensagem_interacao", schema = "public")
@Getter @Setter
public class ZeeTMensagemInteracaoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "id_interacao", nullable = false)
    private Integer idInteracao;

    @Column(name = "mensagem", nullable = false, length = 4000)
    private String mensagem;

    @Column(name = "user_envio")
    private Integer userEnvio;

    @Column(name = "data_envio", nullable = false)
    private LocalDateTime dataEnvio;

    @Column(name = "path_doc", length = 1000)
    private String pathDoc;
}
