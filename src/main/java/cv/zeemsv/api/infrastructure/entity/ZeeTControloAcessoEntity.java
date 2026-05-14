package cv.zeemsv.api.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "zee_t_controlo_acesso", schema = "public")
@Getter @Setter
public class ZeeTControloAcessoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "dm_tp_servico", nullable = false)
    private String dmTpServico;

    @Column(name = "user_acesso", nullable = false)
    private BigDecimal userAcesso;

    @Column(name = "data_acesso", nullable = false)
    private LocalDateTime dataAcesso;

}
