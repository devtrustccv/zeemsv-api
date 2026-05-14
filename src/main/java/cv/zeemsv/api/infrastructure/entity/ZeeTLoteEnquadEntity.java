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
@Table(name = "zee_t_lote_enquad", schema = "public")
@Getter @Setter
public class ZeeTLoteEnquadEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "estado", length = 255)
    private String estado;

    @Column(name = "id_lote")
    private Integer idLote;

    @Column(name = "id_zona_enquad")
    private Integer idZonaEnquad;

}
