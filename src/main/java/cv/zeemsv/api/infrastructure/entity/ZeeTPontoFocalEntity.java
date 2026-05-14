package cv.zeemsv.api.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "zee_t_ponto_focal", schema = "public")
@Getter @Setter
public class ZeeTPontoFocalEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "id_entidade", nullable = false)
    private Integer idEntidade;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @Column(name = "data_registo", nullable = false)
    private LocalDate dataRegisto;

    @Column(name = "user_registo", nullable = false)
    private BigDecimal userRegisto;

    @Column(name = "dm_estado", nullable = false)
    private String dmEstado;

}
