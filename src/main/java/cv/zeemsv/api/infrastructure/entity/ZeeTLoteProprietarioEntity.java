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
@Table(name = "zee_t_lote_proprietario", schema = "public")
@Getter @Setter
public class ZeeTLoteProprietarioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "id_investidor", nullable = false)
    private Integer idInvestidor;

    @Column(name = "id_lote", nullable = false)
    private Integer idLote;

    @Column(name = "origem")
    private String origem;

    @Column(name = "data_registo", nullable = false)
    private LocalDate dataRegisto;

    @Column(name = "user_registo")
    private String userRegisto;

    @Column(name = "dm_estado", nullable = false)
    private String dmEstado;

    @Column(name = "motivo", length = 4000)
    private String motivo;

    @Column(name = "data_fim")
    private LocalDate dataFim;

    @Column(name = "user_fim")
    private String userFim;

}
