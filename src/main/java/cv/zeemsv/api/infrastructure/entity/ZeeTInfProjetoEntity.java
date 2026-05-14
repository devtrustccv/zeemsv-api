package cv.zeemsv.api.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "zee_t_inf_projeto", schema = "public")
@Getter @Setter
public class ZeeTInfProjetoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "id_projeto", nullable = false)
    private Integer idProjeto;

    @Column(name = "volume_negocio", length = 4000)
    private String volumeNegocio;

    @Column(name = "dm_valor_investimento", length = 255)
    private String dmValorInvestimento;

    @Column(name = "dm_classi_negocio", length = 255)
    private String dmClassiNegocio;

    @Column(name = "dm_atividade_pedido", length = 255)
    private String dmAtividadePedido;

    @Column(name = "dm_tipo_estabelecimento", length = 255)
    private String dmTipoEstabelecimento;

    @Column(name = "principios_equip", length = 4000)
    private String principiosEquip;

    @Column(name = "tecnologias", length = 4000)
    private String tecnologias;

    @Column(name = "produtos_fabricados", length = 4000)
    private String produtosFabricados;

    @Column(name = "requisitos", length = 4000)
    private String requisitos;

    @Column(name = "dm_fase_construcao", length = 255)
    private String dmFaseConstrucao;

    @Column(name = "detalhe", length = 400)
    private String detalhe;

    @Column(name = "hora_inicio")
    private LocalTime horaInicio;

    @Column(name = "hora_fim")
    private LocalTime horaFim;

    @Column(name = "carga_horaria")
    private BigDecimal cargaHoraria;

    @Column(name = "gasto_agua")
    private Double gastoAgua;

    @Column(name = "gasto_energia")
    private Double gastoEnergia;

    @Column(name = "dm_pagamento", length = 255)
    private String dmPagamento;

    @Column(name = "n_feminino")
    private BigDecimal nFeminino;

    @Column(name = "n_masculino")
    private BigDecimal nMasculino;

    @Column(name = "n_fun_nacionalidade", length = 255)
    private String nFunNacionalidade;

    @Column(name = "data_registo", nullable = false)
    private LocalDate dataRegisto;

    @Column(name = "user_registo", nullable = false)
    private String userRegisto;

    @Column(name = "data_update")
    private LocalDate dataUpdate;

    @Column(name = "user_update")
    private String userUpdate;

    @Column(name = "tempo_construcao")
    private String tempoConstrucao;

    @Column(name = "categoria_n_empreg")
    private String categoriaNEmpreg;

    @Column(name = "financiamento")
    private String financiamento;

}
