package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTDocRelacaoEntity;
import cv.zeemsv.api.infrastructure.repository.projection.InvestidorDocumentoProjection;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ZeeTDocRelacaoRepository extends JpaRepository<ZeeTDocRelacaoEntity, Integer>, JpaSpecificationExecutor<ZeeTDocRelacaoEntity> {
    List<ZeeTDocRelacaoEntity> findByTipoRelacaoAndIdRelacaoAndEstado(String tipoRelacao, BigDecimal idRelacao, String estado);

    List<ZeeTDocRelacaoEntity> findByTipoRelacaoAndIdRelacaoAndEstadoOrderByDateCreateDescIdDesc(String tipoRelacao, BigDecimal idRelacao, String estado);

    Optional<ZeeTDocRelacaoEntity> findByIdTpDocAndIdRelacaoAndTipoRelacao(Integer idTpDoc, BigDecimal idRelacao, String tipoRelacao);

    Optional<ZeeTDocRelacaoEntity> findFirstByPath(String path);

    @Query(value = """
        select distinct
            d.id as id,
            d.tipo_relacao as tipoRelacao,
            d.id_relacao as idRelacao,
            coalesce(td.nome, d.descricao) as nomeDocumento,
            case
                when d.tipo_relacao in ('INVESTIDOR', 'INVESTIDOR_SINGULAR') then inv.denominacao
                when d.tipo_relacao = 'SOLICITACAO' then coalesce(ts.nome, sol.desc_solic, concat('Solicitacao ', sol.id))
                when d.tipo_relacao = 'ATIVIDADE' then coalesce(
                    atv.tarefa_titulo,
                    atv.nota_titulo,
                    atv.assunto_chamada,
                    atv.assunto_interacao,
                    concat('Atividade ', atv.id)
                )
                when d.tipo_relacao = 'PROJETO' then proj.denominacao
                when d.tipo_relacao = 'NOTIFICACAO' then coalesce(notif.assunto, concat('Notificacao ', notif.id))
                else null
            end as objetoDescricao,
            d.id_doc as idDoc,
            d.id_tp_doc as idTpDoc,
            d.estado as estado,
            d.date_create as dateCreate,
            d.user_create as userCreate,
            d.path as path,
            d.doc_size as docSize,
            d.mimetype as mimetype,
            d.descricao as descricao
        from public.zee_t_doc_relacao d
        left join public.zee_t_tp_doc td on td.id = d.id_tp_doc
        left join public.zee_t_investidor inv
            on d.tipo_relacao in ('INVESTIDOR', 'INVESTIDOR_SINGULAR')
            and inv.id = d.id_relacao::integer
        left join public.zee_t_solicitacao sol
            on d.tipo_relacao = 'SOLICITACAO'
            and sol.id = d.id_relacao::integer
        left join public.zee_t_tp_solicitacao ts on ts.id = sol.id_tp_solicitacao
        left join public.zee_t_atividade atv
            on d.tipo_relacao = 'ATIVIDADE'
            and atv.id = d.id_relacao::integer
        left join public.zee_t_proj_invest proj
            on d.tipo_relacao = 'PROJETO'
            and proj.id = d.id_relacao::integer
        left join gestao_notificacao.t_notificacao notif
            on d.tipo_relacao = 'NOTIFICACAO'
            and notif.id = d.id_relacao::integer
        where d.estado = 'ATIVO'
            and (
                (d.tipo_relacao in ('INVESTIDOR', 'INVESTIDOR_SINGULAR') and d.id_relacao::integer = :idInvestidor)
                or (d.tipo_relacao = 'SOLICITACAO' and exists (
                    select 1
                    from public.zee_t_solicitacao s
                    where s.id = d.id_relacao::integer
                        and s.id_investidor = :idInvestidor
                ))
                or (d.tipo_relacao = 'ATIVIDADE' and exists (
                    select 1
                    from public.zee_t_atividade a
                    where a.id = d.id_relacao::integer
                        and a.id_investidor = :idInvestidor
                ))
                or (d.tipo_relacao = 'PROJETO' and exists (
                    select 1
                    from public.zee_t_proj_invest p
                    where p.id = d.id_relacao::integer
                        and p.id_investidor = :idInvestidor
                ))
                or (d.tipo_relacao = 'NOTIFICACAO' and exists (
                    select 1
                    from gestao_notificacao.t_notificacao_relacao nr
                    where nr.id_notificacao = d.id_relacao::integer
                        and (
                            (nr.tp_relacao in ('INVESTIDOR', 'INVESTIDOR_SINGULAR') and nr.id_relacao::integer = :idInvestidor)
                            or (nr.tp_relacao = 'SOLICITACAO' and exists (
                                select 1
                                from public.zee_t_solicitacao s
                                where s.id = nr.id_relacao::integer
                                    and s.id_investidor = :idInvestidor
                            ))
                            or (nr.tp_relacao = 'ATIVIDADE' and exists (
                                select 1
                                from public.zee_t_atividade a
                                where a.id = nr.id_relacao::integer
                                    and a.id_investidor = :idInvestidor
                            ))
                            or (nr.tp_relacao = 'PROJETO' and exists (
                                select 1
                                from public.zee_t_proj_invest p
                                where p.id = nr.id_relacao::integer
                                    and p.id_investidor = :idInvestidor
                            ))
                        )
                ))
            )
        order by d.date_create desc, d.id desc
        """, nativeQuery = true)
    List<InvestidorDocumentoProjection> findDocumentosByInvestidorId(@Param("idInvestidor") Integer idInvestidor);
}
