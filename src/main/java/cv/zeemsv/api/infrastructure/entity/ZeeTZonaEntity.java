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
@Table(name = "zee_t_zona", schema = "public")
@Getter @Setter
public class ZeeTZonaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "nome", nullable = false, length = 250)
    private String nome;

    @Column(name = "sigla", nullable = false, length = 10)
    private String sigla;

    @Column(name = "estado", nullable = false, length = 10)
    private String estado;

    @Column(name = "local_geog_id", length = 20)
    private String localGeogId;

    @Column(name = "date_create", nullable = false)
    private LocalDateTime dateCreate;

    @Column(name = "user_create", nullable = false, length = 100)
    private String userCreate;

    @Column(name = "nome_norm", length = 255)
    private String nomeNorm;

}
