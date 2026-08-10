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
@Table(name = "zee_t_taxa_desconto", schema = "public")
@Getter @Setter
public class ZeeTTaxaDescontoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "id_taxa", nullable = false)
    private Integer idTaxa;

    @Column(name = "duracao")
    private String duracao;

    @Column(name = "desconto")
    private String desconto;

    @Column(name = "user_registo")
    private String userRegisto;

    @Column(name = "data_registo")
    private LocalDate dataRegisto;
}
