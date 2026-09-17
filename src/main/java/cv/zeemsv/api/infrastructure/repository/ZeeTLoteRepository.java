package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTLoteEntity;
import cv.zeemsv.api.infrastructure.repository.projection.LoteInvestidorProjection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ZeeTLoteRepository extends JpaRepository<ZeeTLoteEntity, Integer>, JpaSpecificationExecutor<ZeeTLoteEntity> {
    @Query("""
        select
            l.id as idLote,
            l.refLote as refLote,
            l.refCm as refCm,
            l.nip as nip,
            l.dmSituacaoCd as dmSituacaoCd,
            l.estado as estado,
            l.idZona as idZona,
            z.nome as zona,
            l.idPark as idPark,
            park.nome as parqueNome,
            park.sigla as parqueSigla,
            park.estado as parqueEstado,
            l.area as area,
            l.areaInicial as areaInicial,
            p.idInvestidor as idInvestidor,
            p.id as idAssociacao,
            null as idProjeto,
            p.origem as origemAssociacao,
            p.dmEstado as dmEstadoAssociacao,
            p.dataRegisto as dataAssociacao,
            p.userRegisto as utilizadorAssociacao,
            null as dmEnquadramento,
            null as formasComercializacao,
            null as projetoDenominacao,
            null as projetoDmRegime,
            null as projetoDmProdutoServico,
            null as projetoDmEstadoProc,
            null as projetoDmSituacao,
            null as projetoDmEstadoProj,
            null as projetoDateCreate
        from ZeeTLoteProprietarioEntity p
        join ZeeTLoteEntity l on l.id = p.idLote
        left join ZeeTZonaEntity z on z.id = l.idZona
        left join ZeeTParkEntity park on park.id = l.idPark
        where p.idInvestidor = :idInvestidor
            and l.estado in ('A', 'ATIVO')
        order by p.dataRegisto desc, p.id desc
        """)
    List<LoteInvestidorProjection> findProprietarioByInvestidorId(@Param("idInvestidor") Integer idInvestidor);

    @Query(value = """
        select
            l.id as idLote,
            l.ref_lote as refLote,
            l.ref_cm as refCm,
            l.nip as nip,
            l.dm_situacao_cd as dmSituacaoCd,
            l.estado as estado,
            l.id_zona as idZona,
            z.nome as zona,
            l.id_park as idPark,
            park.nome as parqueNome,
            park.sigla as parqueSigla,
            park.estado as parqueEstado,
            l.area as area,
            l.area_inicial as areaInicial,
            proj.id_investidor as idInvestidor,
            lp.id as idAssociacao,
            proj.id as idProjeto,
            null as origemAssociacao,
            lp.dm_estado as dmEstadoAssociacao,
            lp.date_create as dataAssociacao,
            lp.user_create::text as utilizadorAssociacao,
            string_agg(distinct ze.dm_enquadramento, ', ') as dmEnquadramento,
            string_agg(distinct ze.dm_enquadramento, ', ') as formasComercializacao,
            proj.denominacao as projetoDenominacao,
            proj.dm_regime as projetoDmRegime,
            proj.dm_produto_servico as projetoDmProdutoServico,
            proj.dm_estado_proc as projetoDmEstadoProc,
            proj.dm_situacao as projetoDmSituacao,
            proj.dm_estado_proj as projetoDmEstadoProj,
            proj.date_create as projetoDateCreate
        from public.zee_t_lote_proj lp
        join public.zee_t_proj_invest proj on proj.id = lp.id_proj
        join public.zee_t_lote l on l.id = lp.id_lote
        left join public.zee_t_zona z on z.id = l.id_zona
        left join public.zee_t_park park on park.id = l.id_park
        left join public.zee_t_proj_lote_enquad ple on ple.id_proj_lote = lp.id and ple.dm_estado = 'A'
        left join public.zee_t_lote_enquad le on le.id = ple.id_lote_enquadr and le.estado in ('A', 'ATIVO')
        left join public.zee_t_zona_enquad ze on ze.id = le.id_zona_enquad and ze.estado in ('A', 'ATIVO')
        where proj.id_investidor = :idInvestidor
            and l.estado in ('A', 'ATIVO')
        group by
            l.id, l.ref_lote, l.ref_cm, l.nip, l.dm_situacao_cd, l.estado, l.id_zona, z.nome,
            l.id_park, park.nome, park.sigla, park.estado, l.area, l.area_inicial,
            proj.id_investidor, lp.id, proj.id, lp.dm_estado, lp.date_create, lp.user_create,
            proj.denominacao, proj.dm_regime, proj.dm_produto_servico, proj.dm_estado_proc,
            proj.dm_situacao, proj.dm_estado_proj, proj.date_create
        order by lp.date_create desc, lp.id desc
        """, nativeQuery = true)
    List<LoteInvestidorProjection> findProjetoByInvestidorId(@Param("idInvestidor") Integer idInvestidor);

    @Query(value = """
        select
            l.id as idLote,
            l.ref_lote as refLote,
            l.ref_cm as refCm,
            l.nip as nip,
            l.dm_situacao_cd as dmSituacaoCd,
            l.estado as estado,
            l.id_zona as idZona,
            z.nome as zona,
            l.id_park as idPark,
            park.nome as parqueNome,
            park.sigla as parqueSigla,
            park.estado as parqueEstado,
            l.area as area,
            l.area_inicial as areaInicial,
            proj.id_investidor as idInvestidor,
            lp.id as idAssociacao,
            proj.id as idProjeto,
            null as origemAssociacao,
            lp.dm_estado as dmEstadoAssociacao,
            lp.date_create as dataAssociacao,
            lp.user_create::text as utilizadorAssociacao,
            string_agg(distinct ze.dm_enquadramento, ', ') as dmEnquadramento,
            string_agg(distinct ze.dm_enquadramento, ', ') as formasComercializacao,
            proj.denominacao as projetoDenominacao,
            proj.dm_regime as projetoDmRegime,
            proj.dm_produto_servico as projetoDmProdutoServico,
            proj.dm_estado_proc as projetoDmEstadoProc,
            proj.dm_situacao as projetoDmSituacao,
            proj.dm_estado_proj as projetoDmEstadoProj,
            proj.date_create as projetoDateCreate
        from public.zee_t_lote_proj lp
        join public.zee_t_proj_invest proj on proj.id = lp.id_proj
        join public.zee_t_lote l on l.id = lp.id_lote
        left join public.zee_t_zona z on z.id = l.id_zona
        left join public.zee_t_park park on park.id = l.id_park
        left join public.zee_t_proj_lote_enquad ple on ple.id_proj_lote = lp.id and ple.dm_estado = 'A'
        left join public.zee_t_lote_enquad le on le.id = ple.id_lote_enquadr and le.estado in ('A', 'ATIVO')
        left join public.zee_t_zona_enquad ze on ze.id = le.id_zona_enquad and ze.estado in ('A', 'ATIVO')
        where lp.id_proj = :idProjecto
            and l.estado in ('A', 'ATIVO')
        group by
            l.id, l.ref_lote, l.ref_cm, l.nip, l.dm_situacao_cd, l.estado, l.id_zona, z.nome,
            l.id_park, park.nome, park.sigla, park.estado, l.area, l.area_inicial,
            proj.id_investidor, lp.id, proj.id, lp.dm_estado, lp.date_create, lp.user_create,
            proj.denominacao, proj.dm_regime, proj.dm_produto_servico, proj.dm_estado_proc,
            proj.dm_situacao, proj.dm_estado_proj, proj.date_create
        order by lp.date_create desc, lp.id desc
        """, nativeQuery = true)
    List<LoteInvestidorProjection> findByProjectoId(@Param("idProjecto") Integer idProjecto);
}
