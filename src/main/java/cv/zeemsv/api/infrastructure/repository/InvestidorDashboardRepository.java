package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.repository.projection.DashboardCountProjection;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

public interface InvestidorDashboardRepository extends Repository<cv.zeemsv.api.infrastructure.entity.ZeeTInvestidorEntity, Integer> {
    @Query(value = """
        select count(distinct lote.id_lote)
        from (
            select lp.id_lote
            from public.zee_t_lote_proprietario lp
            where lp.id_investidor = :idInvestidor
                and lp.dm_estado = 'A'
                and (:ano is null or extract(year from lp.data_registo) = :ano)
                and (:mes is null or extract(month from lp.data_registo) = :mes)
            union
            select lproj.id_lote
            from public.zee_t_lote_proj lproj
            join public.zee_t_proj_invest proj on proj.id = lproj.id_proj
            where proj.id_investidor = :idInvestidor
                and lproj.dm_estado = 'A'
                and (:ano is null or extract(year from lproj.date_create) = :ano)
                and (:mes is null or extract(month from lproj.date_create) = :mes)
        ) lote
        """, nativeQuery = true)
    Long countLotesAtivos(@Param("idInvestidor") Integer idInvestidor, @Param("ano") Integer ano, @Param("mes") Integer mes);

    @Query(value = """
        select count(*)
        from public.zee_t_lote_proprietario lp
        join public.zee_t_lote l on l.id = lp.id_lote
        where lp.id_investidor = :idInvestidor
            and lp.dm_estado = 'A'
            and l.dm_situacao_cd = 'RESERVADO'
            and (:ano is null or extract(year from lp.data_registo) = :ano)
            and (:mes is null or extract(month from lp.data_registo) = :mes)
        """, nativeQuery = true)
    Long countLotesReservados(@Param("idInvestidor") Integer idInvestidor, @Param("ano") Integer ano, @Param("mes") Integer mes);

    @Query(value = """
        select coalesce(sum(lote.valor_comercial), 0)
        from (
            select distinct l.id, l.valor_comercial
            from public.zee_t_lote l
            join (
                select lp.id_lote
                from public.zee_t_lote_proprietario lp
                where lp.id_investidor = :idInvestidor
                    and lp.dm_estado = 'A'
                    and (:ano is null or extract(year from lp.data_registo) = :ano)
                    and (:mes is null or extract(month from lp.data_registo) = :mes)
                union
                select lproj.id_lote
                from public.zee_t_lote_proj lproj
                join public.zee_t_proj_invest proj on proj.id = lproj.id_proj
                where proj.id_investidor = :idInvestidor
                    and lproj.dm_estado = 'A'
                    and (:ano is null or extract(year from lproj.date_create) = :ano)
                    and (:mes is null or extract(month from lproj.date_create) = :mes)
            ) lotes_investidor on lotes_investidor.id_lote = l.id
        ) lote
        """, nativeQuery = true)
    BigDecimal sumValorComercialLotesAtivos(@Param("idInvestidor") Integer idInvestidor, @Param("ano") Integer ano, @Param("mes") Integer mes);

    @Query(value = """
        select count(*)
        from public.zee_t_proj_invest p
        where p.id_investidor = :idInvestidor
            and (:ano is null or extract(year from p.date_create) = :ano)
            and (:mes is null or extract(month from p.date_create) = :mes)
        """, nativeQuery = true)
    Long countProjetos(@Param("idInvestidor") Integer idInvestidor, @Param("ano") Integer ano, @Param("mes") Integer mes);

    @Query(value = """
        select coalesce(p.dm_situacao, 'NAO_INFORMADO') as codigo, count(*) as total
        from public.zee_t_proj_invest p
        where p.id_investidor = :idInvestidor
            and (:ano is null or extract(year from p.date_create) = :ano)
            and (:mes is null or extract(month from p.date_create) = :mes)
        group by coalesce(p.dm_situacao, 'NAO_INFORMADO')
        order by count(*) desc, coalesce(p.dm_situacao, 'NAO_INFORMADO')
        """, nativeQuery = true)
    List<DashboardCountProjection> countProjetosPorSituacao(@Param("idInvestidor") Integer idInvestidor, @Param("ano") Integer ano, @Param("mes") Integer mes);

    @Query(value = """
        select count(*)
        from public.zee_t_solicitacao s
        where s.id_investidor = :idInvestidor
            and (:ano is null or extract(year from s.data_solic) = :ano)
            and (:mes is null or extract(month from s.data_solic) = :mes)
        """, nativeQuery = true)
    Long countProcessos(@Param("idInvestidor") Integer idInvestidor, @Param("ano") Integer ano, @Param("mes") Integer mes);

    @Query(value = """
        select coalesce(s.dm_estado_proc, 'NAO_INFORMADO') as codigo, count(*) as total
        from public.zee_t_solicitacao s
        where s.id_investidor = :idInvestidor
            and (:ano is null or extract(year from s.data_solic) = :ano)
            and (:mes is null or extract(month from s.data_solic) = :mes)
        group by coalesce(s.dm_estado_proc, 'NAO_INFORMADO')
        order by count(*) desc, coalesce(s.dm_estado_proc, 'NAO_INFORMADO')
        """, nativeQuery = true)
    List<DashboardCountProjection> countProcessosPorEstado(@Param("idInvestidor") Integer idInvestidor, @Param("ano") Integer ano, @Param("mes") Integer mes);

    @Query(value = """
        select coalesce(p.etapa_atual, s.etapa_atual, 'NAO_INFORMADO') as codigo, count(*) as total
        from public.zee_t_solicitacao s
        left join public.t_pedido p on p.id = s.id_pedido
        where s.id_investidor = :idInvestidor
            and (:ano is null or extract(year from s.data_solic) = :ano)
            and (:mes is null or extract(month from s.data_solic) = :mes)
        group by coalesce(p.etapa_atual, s.etapa_atual, 'NAO_INFORMADO')
        order by count(*) desc, coalesce(p.etapa_atual, s.etapa_atual, 'NAO_INFORMADO')
        """, nativeQuery = true)
    List<DashboardCountProjection> countProcessosPorEtapa(@Param("idInvestidor") Integer idInvestidor, @Param("ano") Integer ano, @Param("mes") Integer mes);

    @Query(value = """
        select count(*)
        from public.zee_t_atividade a
        where a.id_investidor = :idInvestidor
            and a.agendamento is true
            and upper(a.dm_estado_atividade) = 'PENDENTE'
            and (:ano is null or extract(year from coalesce(a.data_inicio, a.data_create)) = :ano)
            and (:mes is null or extract(month from coalesce(a.data_inicio, a.data_create)) = :mes)
        """, nativeQuery = true)
    Long countAgendamentosPendentes(@Param("idInvestidor") Integer idInvestidor, @Param("ano") Integer ano, @Param("mes") Integer mes);

    @Query(value = """
        select count(*)
        from gestao_notificacao.t_notificacao_relacao r
        join gestao_notificacao.t_notificacao n on n.id = r.id_notificacao
        left join public.zee_t_atividade a on upper(r.tp_relacao) = 'ATIVIDADE' and a.id = r.id_relacao::integer
        left join public.zee_t_solicitacao s on upper(r.tp_relacao) = 'SOLICITACAO' and s.id = r.id_relacao::integer
        left join public.zee_t_proj_invest p on upper(r.tp_relacao) = 'PROJETO' and p.id = r.id_relacao::integer
        left join public.zee_t_lote l on upper(r.tp_relacao) = 'LOTE' and l.id = r.id_relacao::integer
        where n.id_pai is null
            and n.data_leitura is null
            and coalesce(upper(n.flag_leitura), 'N') <> 'S'
            and (:ano is null or extract(year from n.data_registo) = :ano)
            and (:mes is null or extract(month from n.data_registo) = :mes)
            and (
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
            )
        """, nativeQuery = true)
    Long countNotificacoesNaoLidas(@Param("idInvestidor") Integer idInvestidor, @Param("ano") Integer ano, @Param("mes") Integer mes);
}
