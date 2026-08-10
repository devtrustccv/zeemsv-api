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
@Table(name = "zee_t_taxa_condicoes", schema = "public")
@Getter @Setter
public class ZeeTTaxaCondicoesEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "id_taxa", nullable = false)
    private Integer idTaxa;

    @Column(name = "valor")
    private String valor;

    @Column(name = "desconto")
    private String desconto;

    @Column(name = "duracao")
    private String duracao;

    @Column(name = "id_zona")
    private Integer idZona;

    @Column(name = "reserva_apli")
    private String reservaApli;

    @Column(name = "edificio")
    private String edificio;

    @Column(name = "area_min")
    private String areaMin;

    @Column(name = "area_max")
    private String areaMax;

    @Column(name = "atividade")
    private String atividade;

    @Column(name = "pri_vistoria")
    private String priVistoria;

    @Column(name = "user_registo")
    private String userRegisto;

    @Column(name = "data_registo")
    private LocalDate dataRegisto;
}
