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
@Table(name = "zee_t_cobranca_prestacao", schema = "public")
@Getter @Setter
public class ZeeTCobrancaPrestacaoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "id_cobranca")
    private Integer idCobranca;

    @Column(name = "nr_prestacao")
    private String nrPrestacao;

    @Column(name = "valor")
    private String valor;

    @Column(name = "data_vencimento")
    private LocalDate dataVencimento;

    @Column(name = "dm_estado")
    private String dmEstado;

    @Column(name = "user_registo")
    private String userRegisto;

    @Column(name = "data_registo")
    private LocalDate dataRegisto;
}
