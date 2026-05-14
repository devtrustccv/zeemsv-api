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
@Table(name = "zee_t_ordem", schema = "public")
@Getter @Setter
public class ZeeTOrdemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "tipo_ordem", nullable = false)
    private String tipoOrdem;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "cedula")
    private String cedula;

    @Column(name = "concelho")
    private String concelho;

    @Column(name = "endereco")
    private String endereco;

    @Column(name = "email")
    private String email;

    @Column(name = "indicativo_pais")
    private String indicativoPais;

    @Column(name = "telemovel")
    private BigDecimal telemovel;

    @Column(name = "nif")
    private BigDecimal nif;

    @Column(name = "nr_documento")
    private String nrDocumento;

    @Column(name = "nacionalidade")
    private String nacionalidade;

    @Column(name = "numero_inscricao")
    private BigDecimal numeroInscricao;

    @Column(name = "especialidade")
    private String especialidade;

    @Column(name = "dm_estado", nullable = false)
    private String dmEstado;

    @Column(name = "data_registo", nullable = false)
    private LocalDate dataRegisto;

    @Column(name = "user_registo", nullable = false)
    private String userRegisto;

    @Column(name = "dm_tp_doc")
    private String dmTpDoc;

}
