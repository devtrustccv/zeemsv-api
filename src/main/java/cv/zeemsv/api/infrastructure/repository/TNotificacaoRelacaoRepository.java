package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.TNotificacaoRelacaoEntity;
import cv.zeemsv.api.infrastructure.repository.projection.NotificacaoInvestidorProjection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TNotificacaoRelacaoRepository extends JpaRepository<TNotificacaoRelacaoEntity, Integer>, JpaSpecificationExecutor<TNotificacaoRelacaoEntity> {
    @Query(value = """
        select
            r.id as idRelacaoNotificacao,
            r.tp_relacao as tpRelacao,
            r.id_relacao as idRelacao,
            n.id as idNotificacao,
            n.id_pai as idPai,
            n.id_aplicacao as idAplicacao,
            n.id_organica as idOrganica,
            n.user_registo as userRegisto,
            n.data_registo as dataRegisto,
            n.assunto as assunto,
            n.data_envio as dataEnvio,
            n.mensagem as mensagem,
            n.mensagem_confirmacao as mensagemConfirmacao,
            n.email as email,
            n.telemovel as telemovel,
            n.estado as estado,
            n.flag_automatico as flagAutomatico,
            n.flag_sucesso as flagSucesso,
            n.flag_leitura as flagLeitura,
            n.user_leitura as userLeitura,
            n.numero_reenvios as numeroReenvios,
            n.tipo as tipo,
            n.id_relacao as notificacaoIdRelacao,
            n.de as de,
            n.emails_enviados as emailsEnviados,
            n.confirm_recebimento as confirmRecebimento,
            coalesce(an.total_anexos, 0) as totalAnexos,
            a.id as atividadeId,
            a.dm_tipo_atividade as atividadeDmTipoAtividade,
            coalesce(a.tarefa_titulo, a.nota_titulo, a.assunto_chamada) as atividadeTitulo,
            coalesce(a.tarefa_descricao, a.nota_conteudo, a.resumo_chamada) as atividadeResumo,
            a.dm_estado_atividade as atividadeDmEstadoAtividade,
            a.data_create as atividadeDataCreate,
            s.id as solicitacaoId,
            s.id_tp_solicitacao as solicitacaoIdTpSolicitacao,
            ts.nome as solicitacaoTipoNome,
            s.id_processo as solicitacaoIdProcesso,
            s.dm_estado_proc as solicitacaoDmEstadoProc,
            s.data_solic as solicitacaoDataSolic,
            p.id as projetoId,
            p.denominacao as projetoDenominacao,
            p.dm_regime as projetoDmRegime,
            p.dm_produto_servico as projetoDmProdutoServico,
            p.dm_estado_proc as projetoDmEstadoProc,
            p.dm_situacao as projetoDmSituacao,
            l.id as loteId,
            l.ref_lote as loteRefLote,
            l.nip as loteNip,
            l.dm_situacao_cd as loteDmSituacaoCd,
            z.nome as loteZona
        from gestao_notificacao.t_notificacao_relacao r
        join gestao_notificacao.t_notificacao n on n.id = r.id_notificacao
        left join (
            select id_notificacao, count(*) as total_anexos
            from gestao_notificacao.t_notificacao_anexo
            group by id_notificacao
        ) an on an.id_notificacao = n.id
        left join public.zee_t_atividade a on upper(r.tp_relacao) = 'ATIVIDADE' and a.id = r.id_relacao::integer
        left join public.zee_t_solicitacao s on upper(r.tp_relacao) = 'SOLICITACAO' and s.id = r.id_relacao::integer
        left join public.zee_t_tp_solicitacao ts on ts.id = s.id_tp_solicitacao
        left join public.zee_t_proj_invest p on upper(r.tp_relacao) = 'PROJETO' and p.id = r.id_relacao::integer
        left join public.zee_t_lote l on upper(r.tp_relacao) = 'LOTE' and l.id = r.id_relacao::integer
        left join public.zee_t_zona z on z.id = l.id_zona
        where
            (upper(r.tp_relacao) = 'ATIVIDADE' and a.id_investidor = :idInvestidor)
            or (upper(r.tp_relacao) = 'SOLICITACAO' and s.id_investidor = :idInvestidor)
            or (upper(r.tp_relacao) = 'PROJETO' and p.id_investidor = :idInvestidor)
            or (
                upper(r.tp_relacao) = 'LOTE'
                and (
                    exists (
                        select 1 from public.zee_t_lote_proprietario lp
                        where lp.id_lote = l.id and lp.id_investidor = :idInvestidor
                    )
                    or exists (
                        select 1
                        from public.zee_t_lote_proj lproj
                        join public.zee_t_proj_invest proj on proj.id = lproj.id_proj
                        where lproj.id_lote = l.id and proj.id_investidor = :idInvestidor
                    )
                )
            )
        order by n.data_registo desc, n.id desc, r.id desc
        """, nativeQuery = true)
    List<NotificacaoInvestidorProjection> findByInvestidorId(@Param("idInvestidor") Integer idInvestidor);
}
