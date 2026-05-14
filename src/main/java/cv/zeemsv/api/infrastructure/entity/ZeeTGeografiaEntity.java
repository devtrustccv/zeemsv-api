package cv.zeemsv.api.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "zee_t_geografia", schema = "public")
@Getter @Setter
public class ZeeTGeografiaEntity {
    @Id
    @Column(name = "id", nullable = false, length = 255)
    private String id;

    @Column(name = "codigo", length = 255)
    private String codigo;

    @Column(name = "codigo_ine", length = 255)
    private String codigoIne;

    @Column(name = "codigo_iso", length = 255)
    private String codigoIso;

    @Column(name = "concelho", length = 255)
    private String concelho;

    @Column(name = "flag_alter", length = 255)
    private String flagAlter;

    @Column(name = "flg_situacao", length = 255)
    private String flgSituacao;

    @Column(name = "freguesia", length = 255)
    private String freguesia;

    @Column(name = "geogr_id", length = 255)
    private String geogrId;

    @Column(name = "ilha", length = 255)
    private String ilha;

    @Column(name = "nacionalidade", length = 255)
    private String nacionalidade;

    @Column(name = "nivel_detalhe", length = 255)
    private String nivelDetalhe;

    @Column(name = "nome", length = 255)
    private String nome;

    @Column(name = "nome_norm", length = 255)
    private String nomeNorm;

    @Column(name = "nome_oficial", length = 255)
    private String nomeOficial;

    @Column(name = "pais", length = 255)
    private String pais;

    @Column(name = "self_id", length = 255)
    private String selfId;

    @Column(name = "short_nome", length = 255)
    private String shortNome;

    @Column(name = "tp_geog_cd", length = 255)
    private String tpGeogCd;

    @Column(name = "zona", length = 255)
    private String zona;

}
