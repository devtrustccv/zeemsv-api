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
@Table(name = "zee_t_tp_doc", schema = "public")
@Getter @Setter
public class ZeeTTpDocEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "nome", nullable = false, length = 200)
    private String nome;

    @Column(name = "codigo", nullable = false, length = 100)
    private String codigo;

    @Column(name = "estado", nullable = false, length = 10)
    private String estado;

    @Column(name = "obrigatorio", nullable = false)
    private Boolean obrigatorio;

    @Column(name = "tipo_relacao", nullable = false, length = 20)
    private String tipoRelacao;

    @Column(name = "data_registo", nullable = false)
    private LocalDateTime dataRegisto;

    @Column(name = "user_registo", nullable = false, length = 100)
    private String userRegisto;

}
