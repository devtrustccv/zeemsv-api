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
            l.nip as nip,
            l.dmSituacaoCd as dmSituacaoCd,
            l.estado as estado,
            l.idZona as idZona,
            z.nome as zona,
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
        where p.idInvestidor = :idInvestidor
        order by p.dataRegisto desc, p.id desc
        """)
    List<LoteInvestidorProjection> findProprietarioByInvestidorId(@Param("idInvestidor") Integer idInvestidor);

    @Query("""
        select
            l.id as idLote,
            l.refLote as refLote,
            l.nip as nip,
            l.dmSituacaoCd as dmSituacaoCd,
            l.estado as estado,
            l.idZona as idZona,
            z.nome as zona,
            l.area as area,
            l.areaInicial as areaInicial,
            proj.idInvestidor as idInvestidor,
            lp.id as idAssociacao,
            proj.id as idProjeto,
            null as origemAssociacao,
            lp.dmEstado as dmEstadoAssociacao,
            lp.dateCreate as dataAssociacao,
            str(lp.userCreate) as utilizadorAssociacao,
            ze.dmEnquadramento as dmEnquadramento,
            proj.denominacao as projetoDenominacao,
            proj.dmRegime as projetoDmRegime,
            proj.dmProdutoServico as projetoDmProdutoServico,
            proj.dmEstadoProc as projetoDmEstadoProc,
            proj.dmSituacao as projetoDmSituacao,
            proj.dmEstadoProj as projetoDmEstadoProj,
            proj.dateCreate as projetoDateCreate
        from ZeeTLoteProjEntity lp
        join ZeeTProjInvestEntity proj on proj.id = lp.idProj
        join ZeeTLoteEntity l on l.id = lp.idLote
        left join ZeeTZonaEntity z on z.id = l.idZona
        left join ZeeTProjLoteEnquadEntity ple on ple.idProjLote = lp.id
        left join ZeeTLoteEnquadEntity le on le.id = ple.idLoteEnquadr
        left join ZeeTZonaEnquadEntity ze on ze.id = le.idZonaEnquad
        where proj.idInvestidor = :idInvestidor
        order by lp.dateCreate desc, lp.id desc
        """)
    List<LoteInvestidorProjection> findProjetoByInvestidorId(@Param("idInvestidor") Integer idInvestidor);
}
