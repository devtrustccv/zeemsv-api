package cv.zeemsv.api.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "t_notificacao_relacao", schema = "gestao_notificacao")
@Getter @Setter
public class TNotificacaoRelacaoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "tp_relacao", nullable = false)
    private String tpRelacao;

    @Column(name = "id_relacao", nullable = false)
    private BigDecimal idRelacao;

    @Column(name = "id_notificacao", nullable = false)
    private Integer idNotificacao;
}
