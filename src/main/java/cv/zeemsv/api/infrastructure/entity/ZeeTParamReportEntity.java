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
@Table(name = "zee_t_param_report", schema = "public")
@Getter @Setter
public class ZeeTParamReportEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "codigo_postal", length = 255)
    private String codigoPostal;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "id_doc_manual", length = 255)
    private String idDocManual;

    @Column(name = "id_logo", length = 255)
    private String idLogo;

    @Column(name = "link_portal", length = 255)
    private String linkPortal;

    @Column(name = "rua", length = 255)
    private String rua;

    @Column(name = "telefone", length = 255)
    private String telefone;

    @Column(name = "telemovel", length = 255)
    private String telemovel;

}
