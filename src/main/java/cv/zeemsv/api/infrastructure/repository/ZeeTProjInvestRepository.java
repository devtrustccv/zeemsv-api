package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTProjInvestEntity;
import cv.zeemsv.api.infrastructure.repository.projection.ProjetoInvestidorProjection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ZeeTProjInvestRepository extends JpaRepository<ZeeTProjInvestEntity, Integer>, JpaSpecificationExecutor<ZeeTProjInvestEntity> {
    List<ZeeTProjInvestEntity> findByIdInvestidorOrderByDateCreateDesc(Integer idInvestidor);

    @Query("""
        select
            p.id as id,
            p.estado as estado,
            p.denominacao as denominacao,
            p.dmEnquadrameno as dmEnquadrameno,
            p.dmRegime as dmRegime,
            p.dmProdutoServico as dmProdutoServico,
            p.dmEstadoInstall as dmEstadoInstall,
            p.dmEstadoProc as dmEstadoProc,
            p.dateCreate as dateCreate,
            p.userCreate as userCreate,
            p.dmDocFalta as dmDocFalta,
            p.idInvestidorCae as idInvestidorCae,
            p.dmSituacao as dmSituacao,
            p.idInvestidor as idInvestidor,
            p.dmEstadoProj as dmEstadoProj,
            p.dataDesistencia as dataDesistencia,
            p.userDesistencia as userDesistencia,
            p.motivo as motivo,
            coalesce(c.descricao, ic.cae) as atividadePrincipal,
            ic.setorCae as atividadePrincipalSetor
        from ZeeTProjInvestEntity p
        left join ZeeTInvestidorCaeEntity ic on ic.id = p.idInvestidorCae
        left join TblCaeEntity c on c.codigo = ic.cae
        where p.idInvestidor = :idInvestidor
        order by p.dateCreate desc, p.id desc
        """)
    List<ProjetoInvestidorProjection> findDetalheByInvestidorId(@Param("idInvestidor") Integer idInvestidor);
}
