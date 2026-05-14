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
@Table(name = "zee_t_investidor", schema = "public")
@Getter @Setter
public class ZeeTInvestidorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "denominacao", nullable = false, length = 255)
    private String denominacao;

    @Column(name = "matricula", length = 255)
    private String matricula;

    @Column(name = "dm_natureza_juridica", length = 20)
    private String dmNaturezaJuridica;

    @Column(name = "setor", length = 250)
    private String setor;

    @Column(name = "sede")
    private String sede;

    @Column(name = "dm_classificacao")
    private String dmClassificacao;

    @Column(name = "data_constituicao")
    private LocalDate dataConstituicao;

    @Column(name = "phone")
    private BigDecimal phone;

    @Column(name = "indicativo_pais")
    private String indicativoPais;

    @Column(name = "telemovel")
    private BigDecimal telemovel;

    @Column(name = "email", length = 50)
    private String email;

    @Column(name = "site", length = 60)
    private String site;

    @Column(name = "flag_rec", nullable = false, length = 4)
    private String flagRec;

    @Column(name = "dm_estado", length = 20)
    private String dmEstado;

    @Column(name = "link_reg_comercial", length = 200)
    private String linkRegComercial;

    @Column(name = "date_create", nullable = false)
    private LocalDate dateCreate;

    @Column(name = "user_create", nullable = false)
    private BigDecimal userCreate;

    @Column(name = "date_update")
    private LocalDate dateUpdate;

    @Column(name = "user_update")
    private BigDecimal userUpdate;

    @Column(name = "forma_obrigar", length = 4000)
    private String formaObrigar;

    @Column(name = "capital_social")
    private BigDecimal capitalSocial;

    @Column(name = "pais_origem", nullable = false, length = 200)
    private String paisOrigem;

    @Column(name = "endereco", length = 500)
    private String endereco;

    @Column(name = "nif", nullable = false, length = 200)
    private String nif;

    @Column(name = "flag_servico", length = 4)
    private String flagServico;

    @Column(name = "dm_idoma", nullable = false, length = 4)
    private String dmIdoma;

    @Column(name = "dm_tipo_investidor", length = 20)
    private String dmTipoInvestidor;

    @Column(name = "dm_genero", length = 20)
    private String dmGenero;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @Column(name = "dm_estado_civil", length = 20)
    private String dmEstadoCivil;

    @Column(name = "profissao", length = 500)
    private String profissao;

    @Column(name = "nr_documento", length = 250)
    private String nrDocumento;

    @Column(name = "moeda", length = 20)
    private String moeda;

    @Column(name = "dm_geenro", length = 20)
    private String dmGeenro;

}
