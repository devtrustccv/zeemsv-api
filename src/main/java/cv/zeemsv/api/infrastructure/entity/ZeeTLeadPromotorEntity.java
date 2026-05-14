package cv.zeemsv.api.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "zee_t_lead_promotor", schema = "public")
@Getter @Setter
public class ZeeTLeadPromotorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "capital_social", length = 255)
    private String capitalSocial;

    @Column(name = "data_constituicao")
    private LocalDate dataConstituicao;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @Column(name = "date_create")
    private LocalDateTime dateCreate;

    @Column(name = "date_update")
    private LocalDateTime dateUpdate;

    @Column(name = "denominacao", length = 255)
    private String denominacao;

    @Column(name = "dm_estado", length = 255)
    private String dmEstado;

    @Column(name = "dm_estado_civil", length = 255)
    private String dmEstadoCivil;

    @Column(name = "dm_genero", length = 255)
    private String dmGenero;

    @Column(name = "dm_idioma", length = 255)
    private String dmIdioma;

    @Column(name = "dm_natureza_juridica", length = 255)
    private String dmNaturezaJuridica;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "endereco", length = 255)
    private String endereco;

    @Column(name = "forma_obrigar", length = 4000)
    private String formaObrigar;

    @Column(name = "indicativo_pais")
    private Integer indicativoPais;

    @Column(name = "link_reg_comercial", length = 255)
    private String linkRegComercial;

    @Column(name = "matricula", length = 255)
    private String matricula;

    @Column(name = "nif", length = 255)
    private String nif;

    @Column(name = "nr_documento", length = 255)
    private String nrDocumento;

    @Column(name = "pais_origem", length = 255)
    private String paisOrigem;

    @Column(name = "phone")
    private BigDecimal phone;

    @Column(name = "profissao", length = 50)
    private String profissao;

    @Column(name = "sede", length = 255)
    private String sede;

    @Column(name = "setor", length = 500)
    private String setor;

    @Column(name = "site", length = 255)
    private String site;

    @Column(name = "telemovel")
    private BigDecimal telemovel;

    @Column(name = "tipo_investidor", length = 255)
    private String tipoInvestidor;

    @Column(name = "user_create")
    private Integer userCreate;

    @Column(name = "user_update")
    private Integer userUpdate;

}
