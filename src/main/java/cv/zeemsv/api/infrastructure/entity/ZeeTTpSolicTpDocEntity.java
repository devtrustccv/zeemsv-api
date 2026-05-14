package cv.zeemsv.api.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "zee_t_tp_solic_tp_doc", schema = "public")
@Getter @Setter
public class ZeeTTpSolicTpDocEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "id_tp_solic", nullable = false)
    private Integer idTpSolic;

    @Column(name = "id_tp_doc")
    private Integer idTpDoc;

    @Column(name = "requisito")
    private String requisito;

    @Column(name = "flag_obrigatorio")
    private String flagObrigatorio;

    @Column(name = "ped_resp")
    private String pedResp;

    @Column(name = "data_registro")
    private LocalDate dataRegistro;

    @Column(name = "dm_estado", length = 255)
    private String dmEstado;

    @Column(name = "user_registro")
    private Integer userRegistro;

}
