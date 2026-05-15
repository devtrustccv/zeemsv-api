package cv.zeemsv.api.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "t_notificacao_anexo", schema = "gestao_notificacao")
@Getter @Setter
public class TNotificacaoAnexoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "id_notificacao", nullable = false)
    private Integer idNotificacao;

    @Column(name = "id_doc", nullable = false)
    private Integer idDoc;
}
