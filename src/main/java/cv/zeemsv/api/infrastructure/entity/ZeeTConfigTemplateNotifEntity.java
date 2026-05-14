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
@Table(name = "zee_t_config_template_notif", schema = "public")
@Getter @Setter
public class ZeeTConfigTemplateNotifEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "assunto", length = 255)
    private String assunto;

    @Column(name = "codigo", length = 255)
    private String codigo;

    @Column(name = "data_create")
    private LocalDate dataCreate;

    @Column(name = "dm_estado", nullable = false, length = 255)
    private String dmEstado;

    @Column(name = "idioma", length = 255)
    private String idioma;

    @Column(name = "template_msg", length = 4000)
    private String templateMsg;

    @Column(name = "user_create")
    private Integer userCreate;

    @Column(name = "id_pai")
    private BigDecimal idPai;

}
