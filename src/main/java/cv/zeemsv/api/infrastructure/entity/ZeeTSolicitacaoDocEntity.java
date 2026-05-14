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
@Table(name = "zee_t_solicitacao_doc", schema = "public")
@Getter @Setter
public class ZeeTSolicitacaoDocEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "id_solicitacao", nullable = false)
    private Integer idSolicitacao;

    @Column(name = "id_tp_solic_tp_doc", nullable = false)
    private Integer idTpSolicTpDoc;

    @Column(name = "id_processo", nullable = false)
    private BigDecimal idProcesso;

    @Column(name = "id_etapa", nullable = false)
    private BigDecimal idEtapa;

    @Column(name = "data_registo", nullable = false)
    private LocalDate dataRegisto;

    @Column(name = "user_registo", nullable = false)
    private String userRegisto;

    @Column(name = "path")
    private String path;

}
