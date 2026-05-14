package cv.zeemsv.api.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "zee_t_atividade", schema = "public")
@Getter @Setter
public class ZeeTAtividadeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "id_investidor")
    private Integer idInvestidor;

    @Column(name = "id_projeto", length = 500)
    private String idProjeto;

    @Column(name = "id_representante")
    private Integer idRepresentante;

    @Column(name = "id_relacao")
    private Integer idRelacao;

    @Column(name = "dm_tipo_atividade", nullable = false, length = 255)
    private String dmTipoAtividade;

    @Column(name = "dm_tag", length = 4000)
    private String dmTag;

    @Column(name = "data_conclusao")
    private LocalDate dataConclusao;

    @Column(name = "user_responsavel", nullable = false)
    private Integer userResponsavel;

    @Column(name = "dm_estado_atividade", length = 20)
    private String dmEstadoAtividade;

    @Column(name = "id_pessoa_cont")
    private Integer idPessoaCont;

    @Column(name = "dm_tp_chamada")
    private String dmTpChamada;

    @Column(name = "dm_resultado")
    private String dmResultado;

    @Column(name = "hora_chamada")
    private LocalTime horaChamada;

    @Column(name = "assunto_chamada")
    private String assuntoChamada;

    @Column(name = "resumo_chamada", length = 4000)
    private String resumoChamada;

    @Column(name = "nota_titulo")
    private String notaTitulo;

    @Column(name = "nota_conteudo", length = 4000)
    private String notaConteudo;

    @Column(name = "tarefa_titulo")
    private String tarefaTitulo;

    @Column(name = "tarefa_descricao", length = 4000)
    private String tarefaDescricao;

    @Column(name = "dm_prioridade")
    private String dmPrioridade;

    @Column(name = "data_inicio")
    private LocalDate dataInicio;

    @Column(name = "data_fim")
    private LocalDate dataFim;

    @Column(name = "dm_estado", nullable = false)
    private String dmEstado;

    @Column(name = "user_registo", nullable = false)
    private Integer userRegisto;

    @Column(name = "data_create", nullable = false)
    private LocalDate dataCreate;

    @Column(name = "tipo_relacao", length = 255)
    private String tipoRelacao;

}
