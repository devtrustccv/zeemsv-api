package cv.zeemsv.api.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "zee_t_doc_relacao", schema = "public")
@Getter @Setter
public class ZeeTDocRelacaoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "tipo_relacao", nullable = false, length = 250)
    private String tipoRelacao;

    @Column(name = "id_relacao", nullable = false)
    private BigDecimal idRelacao;

    @Column(name = "id_doc", length = 128)
    private String idDoc;

    @Column(name = "id_tp_doc")
    private Integer idTpDoc;

    @Column(name = "estado", nullable = false, length = 10)
    private String estado;

    @Column(name = "date_create", nullable = false)
    private LocalDateTime dateCreate;

    @Column(name = "user_create", nullable = false)
    private String userCreate;

    @Column(name = "path", length = 1000)
    private String path;

    @Column(name = "doc_size")
    private BigDecimal docSize;

    @Column(name = "mimetype", length = 255)
    private String mimetype;

    @Column(name = "descricao")
    private String descricao;

}
