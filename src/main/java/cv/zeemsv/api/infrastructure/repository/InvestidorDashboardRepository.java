package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.repository.projection.DashboardCountProjection;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

public interface InvestidorDashboardRepository extends Repository<cv.zeemsv.api.infrastructure.entity.ZeeTInvestidorEntity, Integer> {
    @Query(value = """
        select count(*)
        from public.zee_t_lote_proprietario lp
        where lp.id_investidor = :idInvestidor
            and lp.dm_estado = 'A'
        """, nativeQuery = true)
    Long countLotesAtivos(@Param("idInvestidor") Integer idInvestidor);

    @Query(value = """
        select count(*)
        from public.zee_t_lote_proprietario lp
        join public.zee_t_lote l on l.id = lp.id_lote
        where lp.id_investidor = :idInvestidor
            and lp.dm_estado = 'A'
            and l.dm_situacao_cd = 'RESERVADO'
        """, nativeQuery = true)
    Long countLotesReservados(@Param("idInvestidor") Integer idInvestidor);

    @Query(value = """
        select count(*)
        from public.zee_t_proj_invest p
        where p.id_investidor = :idInvestidor
        """, nativeQuery = true)
    Long countProjetos(@Param("idInvestidor") Integer idInvestidor);

    @Query(value = """
        select coalesce(p.dm_situacao, 'NAO_INFORMADO') as codigo, count(*) as total
        from public.zee_t_proj_invest p
        where p.id_investidor = :idInvestidor
        group by coalesce(p.dm_situacao, 'NAO_INFORMADO')
        order by count(*) desc, coalesce(p.dm_situacao, 'NAO_INFORMADO')
        """, nativeQuery = true)
    List<DashboardCountProjection> countProjetosPorSituacao(@Param("idInvestidor") Integer idInvestidor);

    @Query(value = """
        select count(*)
        from public.zee_t_solicitacao s
        where s.id_investidor = :idInvestidor
        """, nativeQuery = true)
    Long countProcessos(@Param("idInvestidor") Integer idInvestidor);

    @Query(value = """
        select coalesce(s.dm_estado_proc, 'NAO_INFORMADO') as codigo, count(*) as total
        from public.zee_t_solicitacao s
        where s.id_investidor = :idInvestidor
        group by coalesce(s.dm_estado_proc, 'NAO_INFORMADO')
        order by count(*) desc, coalesce(s.dm_estado_proc, 'NAO_INFORMADO')
        """, nativeQuery = true)
    List<DashboardCountProjection> countProcessosPorEstado(@Param("idInvestidor") Integer idInvestidor);

    @Query(value = """
        select coalesce(p.etapa_atual, s.etapa_atual, 'NAO_INFORMADO') as codigo, count(*) as total
        from public.zee_t_solicitacao s
        left join public.t_pedido p on p.id = s.id_pedido
        where s.id_investidor = :idInvestidor
        group by coalesce(p.etapa_atual, s.etapa_atual, 'NAO_INFORMADO')
        order by count(*) desc, coalesce(p.etapa_atual, s.etapa_atual, 'NAO_INFORMADO')
        """, nativeQuery = true)
    List<DashboardCountProjection> countProcessosPorEtapa(@Param("idInvestidor") Integer idInvestidor);
}
