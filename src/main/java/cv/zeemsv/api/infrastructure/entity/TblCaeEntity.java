package cv.zeemsv.api.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tbl_cae", schema = "public")
@Getter @Setter
public class TblCaeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "codigo", length = 255)
    private String codigo;

    @Column(name = "crpcae_id")
    private BigDecimal crpcaeId;

    @Column(name = "descricao", length = 255)
    private String descricao;

    @Column(name = "flag_situacao", length = 255)
    private String flagSituacao;

    @Column(name = "nivel", length = 255)
    private String nivel;

}
