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
@Table(name = "zee_t_lead_fases", schema = "public")
@Getter @Setter
public class ZeeTLeadFasesEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "date_create")
    private LocalDateTime dateCreate;

    @Column(name = "descricao", length = 255)
    private String descricao;

    @Column(name = "duracao")
    private Integer duracao;

    @Column(name = "fase_estado", length = 255)
    private String faseEstado;

    @Column(name = "fecho_lead")
    private Integer fechoLead;

    @Column(name = "ordem")
    private Integer ordem;

    @Column(name = "user_create")
    private Integer userCreate;

}
