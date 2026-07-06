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
@Table(name = "zee_t_atividade", schema = "public")
@Getter @Setter
public class ZeeTAtividadeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "id_investidor", precision = 20, scale = 0)
    private BigDecimal idInvestidor;

    @Column(name = "id_projeto", length = 500)
    private String idProjeto;

    @Column(name = "id_representante", precision = 19, scale = 2)
    private BigDecimal idRepresentante;

    @Column(name = "id_relacao", precision = 20, scale = 0)
    private BigDecimal idRelacao;

    @Column(name = "dm_tipo_atividade", nullable = false, length = 255)
    private String dmTipoAtividade;

    @Column(name = "dm_tag", length = 4000)
    private String dmTag;

    @Column(name = "data_conclusao")
    private LocalDate dataConclusao;

    @Column(name = "user_responsavel")
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

    @Column(name = "user_registo")
    private Integer userRegisto;

    @Column(name = "data_create", nullable = false)
    private LocalDate dataCreate;

    @Column(name = "tipo_relacao", length = 255)
    private String tipoRelacao;

    @Column(name = "agendamento")
    private Boolean agendamento;

    @Column(name = "tp_agendamento")
    private String tpAgendamento;

    @Column(name = "hora_inicio")
    private String horaInicio;

    @Column(name = "hora_fim")
    private String horaFim;

    @Column(name = "tipo_interacao", length = 255)
    private String tipoInteracao;

    @Column(name = "dm_estado_interacao", length = 50)
    private String dmEstadoInteracao;

    @Column(name = "assunto_interacao", length = 500)
    private String assuntoInteracao;

    @Column(name = "mensagem_interacao", length = 4000)
    private String mensagemInteracao;

    @Column(name = "user_resposta")
    private Integer userResposta;

    @Column(name = "data_resposta")
    private LocalDate dataResposta;

    @Column(name = "mensagem_resposta", length = 4000)
    private String mensagemResposta;

    @Column(name = "id_user")
    private Integer idUser;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "telefone", length = 50)
    private String telefone;

    @Column(name = "nome", length = 255)
    private String nome;

}
