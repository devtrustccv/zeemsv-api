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
@Table(name = "zee_t_lote_relacao", schema = "public")
@Getter @Setter
public class ZeeTLoteRelacaoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "forma_registo", nullable = false, length = 20)
    private String formaRegisto;

    @Column(name = "lote_origem_id", nullable = false)
    private Integer loteOrigemId;

    @Column(name = "lote_destino_id", nullable = false)
    private Integer loteDestinoId;

    @Column(name = "estado", nullable = false, length = 10)
    private String estado;

    @Column(name = "date_create", nullable = false)
    private LocalDateTime dateCreate;

    @Column(name = "user_create", nullable = false, length = 100)
    private String userCreate;

    @Column(name = "area_cedida")
    private BigDecimal areaCedida;

    @Column(name = "area_disponivel")
    private BigDecimal areaDisponivel;

}
