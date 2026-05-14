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
@Table(name = "zee_t_park", schema = "public")
@Getter @Setter
public class ZeeTParkEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "id_zona", nullable = false)
    private Integer idZona;

    @Column(name = "nome", nullable = false, length = 200)
    private String nome;

    @Column(name = "sigla", nullable = false, length = 10)
    private String sigla;

    @Column(name = "estado", nullable = false, length = 10)
    private String estado;

    @Column(name = "date_create", nullable = false)
    private LocalDateTime dateCreate;

    @Column(name = "user_create", nullable = false, length = 100)
    private String userCreate;

}
