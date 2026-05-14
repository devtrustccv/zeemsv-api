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
@Table(name = "zee_t_zona_cord", schema = "public")
@Getter @Setter
public class ZeeTZonaCordEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "id_zona")
    private Integer idZona;

    @Column(name = "vertice", nullable = false)
    private BigDecimal vertice;

    @Column(name = "coordenada_x", nullable = false, length = 4000)
    private String coordenadaX;

    @Column(name = "coordenada_y", nullable = false, length = 4000)
    private String coordenadaY;

    @Column(name = "dm_estado", nullable = false)
    private String dmEstado;

    @Column(name = "user_registo", nullable = false)
    private String userRegisto;

    @Column(name = "data_registo", nullable = false)
    private LocalDate dataRegisto;

    @Column(name = "id_lote")
    private Integer idLote;

}
