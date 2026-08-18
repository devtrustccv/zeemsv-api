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
@Table(name = "zee_t_solicitacao_cobranca", schema = "public")
@Getter @Setter
public class ZeeTSolicitacaoCobrancaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "id_solicitacao", nullable = false)
    private Integer idSolicitacao;

    @Column(name = "id_cobranca", nullable = false)
    private Integer idCobranca;
}
