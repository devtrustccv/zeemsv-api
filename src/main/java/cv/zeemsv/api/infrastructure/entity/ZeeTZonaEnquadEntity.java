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
@Table(name = "zee_t_zona_enquad", schema = "public")
@Getter @Setter
public class ZeeTZonaEnquadEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "dm_enquadramento", length = 255)
    private String dmEnquadramento;

    @Column(name = "estado", length = 255)
    private String estado;

    @Column(name = "id_zona")
    private Integer idZona;

}
