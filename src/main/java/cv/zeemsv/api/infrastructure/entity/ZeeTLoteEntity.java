package cv.zeemsv.api.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "zee_t_lote", schema = "public")
@Getter @Setter
public class ZeeTLoteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "id_zona", nullable = false)
    private Integer idZona;

    @Column(name = "id_park")
    private Integer idPark;

    @Column(name = "area_inicial", nullable = false)
    private BigDecimal areaInicial;

    @Column(name = "area", nullable = false)
    private BigDecimal area;

    @Column(name = "ref_cm", length = 50)
    private String refCm;

    @Column(name = "ref_lote", nullable = false, length = 50)
    private String refLote;

    @Column(name = "dm_situacao_cd", nullable = false, length = 11)
    private String dmSituacaoCd;

    @Column(name = "estado", nullable = false, length = 10)
    private String estado;

    @Column(name = "forma_criacao", length = 20)
    private String formaCriacao;

    @Column(name = "date_create", nullable = false)
    private LocalDateTime dateCreate;

    @Column(name = "user_create", nullable = false)
    private String userCreate;

    @Column(name = "nip", nullable = false)
    private String nip;

    @Column(name = "date_cancel_pub")
    private LocalDateTime dateCancelPub;

    @Column(name = "date_publicacao")
    private LocalDateTime datePublicacao;

    @Column(name = "date_validacao")
    private LocalDateTime dateValidacao;

    @Column(name = "dm_disponibilidade", length = 255)
    private String dmDisponibilidade;

    @Column(name = "dm_formalizacao", length = 255)
    private String dmFormalizacao;

    @Column(name = "publicado")
    private Boolean publicado;

    @Column(name = "ref_cm_norm", length = 255)
    private String refCmNorm;

    @Column(name = "ref_lote_norm", length = 255)
    private String refLoteNorm;

    @Column(name = "user_cancel_pub", length = 255)
    private String userCancelPub;

    @Column(name = "user_publicacao", length = 255)
    private String userPublicacao;

    @Column(name = "user_validacao", length = 255)
    private String userValidacao;

    @Column(name = "validado")
    private Boolean validado;

    @Column(name = "valor_comercial")
    private BigDecimal valorComercial;

    @Column(name = "valor_direito_superficie")
    private BigDecimal valorDireitoSuperficie;

}
