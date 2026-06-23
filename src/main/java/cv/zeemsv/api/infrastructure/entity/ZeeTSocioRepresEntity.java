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
@Table(name = "zee_t_socio_repres", schema = "public")
@Getter @Setter
public class ZeeTSocioRepresEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "id_investidor")
    private Integer idInvestidor;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Column(name = "nacionalidade", length = 50)
    private String nacionalidade;

    @Column(name = "nif", length = 20)
    private String nif;

    @Column(name = "tipo_doc", length = 20)
    private String tipoDoc;

    @Column(name = "nr_doc", length = 20)
    private String nrDoc;

    @Column(name = "dm_tp_representante", length = 20)
    private String dmTpRepresentante;

    @Column(name = "telefone")
    private BigDecimal telefone;

    @Column(name = "telemovel")
    private BigDecimal telemovel;

    @Column(name = "email")
    private String email;

    @Column(name = "flag_socio")
    private Boolean flagSocio;

    @Column(name = "flag_representante")
    private Boolean flagRepresentante;

    @Column(name = "dm_principal")
    private String dmPrincipal;

    @Column(name = "estado")
    private String estado;

    @Column(name = "date_create", nullable = false)
    private LocalDate dateCreate;

    @Column(name = "user_create")
    private BigDecimal userCreate;

    @Column(name = "indicativo_pais")
    private String indicativoPais;

    @Column(name = "id_user")
    private Integer idUser;

}
