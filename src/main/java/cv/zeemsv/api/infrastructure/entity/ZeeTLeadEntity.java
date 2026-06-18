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
@Table(name = "zee_t_lead", schema = "public")
@Getter @Setter
public class ZeeTLeadEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "area_pretendida")
    private Integer areaPretendida;

    @Column(name = "canal_entrada", length = 255)
    private String canalEntrada;

    @Column(name = "codigo", length = 255)
    private String codigo;

    @Column(name = "data_identificacao")
    private LocalDate dataIdentificacao;

    @Column(name = "data_ultimo_contacto")
    private LocalDate dataUltimoContacto;

    @Column(name = "date_create")
    private LocalDateTime dateCreate;

    @Column(name = "date_update")
    private LocalDateTime dateUpdate;

    @Column(name = "dm_estado", length = 255)
    private String dmEstado;

    @Column(name = "gestor_lead")
    private Integer gestorLead;

    @Column(name = "grau_urgencia", length = 255)
    private String grauUrgencia;

    @Column(name = "investidor_existente", length = 255)
    private String investidorExistente;

    @Column(name = "lotes", length = 255)
    private String lotes;

    @Column(name = "motivo_contacto", length = 255)
    private String motivoContacto;

    @Column(name = "motivo_nao_avanco", length = 255)
    private String motivoNaoAvanco;

    @Column(name = "observacao", length = 255)
    private String observacao;

    @Column(name = "ponto_focal", length = 255)
    private String pontoFocal;

    @Column(name = "probabilidade", length = 255)
    private String probabilidade;

    @Column(name = "tecnico_atendimento")
    private Integer tecnicoAtendimento;

    @Column(name = "tipo_negocio", length = 255)
    private String tipoNegocio;

    @Column(name = "user_create")
    private Integer userCreate;

    @Column(name = "user_update")
    private Integer userUpdate;

    @Column(name = "zona", length = 255)
    private String zona;

    @Column(name = "id_fase")
    private Integer idFase;

    @Column(name = "id_investidor")
    private Integer idInvestidor;

    @Column(name = "id_promotor")
    private Integer idPromotor;

    @Column(name = "flag_conversao")
    private Integer flagConversao;

    @Column(name = "id_projeto")
    private BigDecimal idProjeto;

}
