package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTTpSolicitacaoEntity;
import cv.zeemsv.api.infrastructure.repository.projection.ServicoProjection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ZeeTTpSolicitacaoRepository extends JpaRepository<ZeeTTpSolicitacaoEntity, Integer>, JpaSpecificationExecutor<ZeeTTpSolicitacaoEntity> {
    List<ZeeTTpSolicitacaoEntity> findAllByOrderByNomeAsc();

    @Query("""
        select
            s.id as id,
            s.nome as nome,
            s.dmTipoSolicitacao as dmTipoSolicitacao,
            s.descricao as descricao,
            s.msgPedido as msgPedido,
            s.prazoDia as prazoDia,
            s.flagObrigatorio as flagObrigatorio,
            s.codigo as codigo,
            s.dmEstado as dmEstado,
            s.idEntExterna as idEntExterna,
            s.possuiTaxa as possuiTaxa,
            e.denominacao as entidadeDenominacao,
            e.sigla as entidadeSigla,
            e.dmTipoEnt as entidadeDmTipoEnt
        from ZeeTTpSolicitacaoEntity s
        left join ZeeTEntidadeExternaEntity e on e.id = s.idEntExterna
        order by s.nome
        """)
    List<ServicoProjection> findAllServicos();

    @Query("""
        select distinct s
        from ZeeTTpSolicitacaoEntity s
        join ZeeTTpSolicRepreEntity r on r.idTpSolic = s.id
        where lower(r.dmTpRepresentante) = lower(:dmTpRepresentante)
        order by s.nome
        """)
    List<ZeeTTpSolicitacaoEntity> findByTipoRepresentante(@Param("dmTpRepresentante") String dmTpRepresentante);

    @Query("""
        select distinct
            s.id as id,
            s.nome as nome,
            s.dmTipoSolicitacao as dmTipoSolicitacao,
            s.descricao as descricao,
            s.msgPedido as msgPedido,
            s.prazoDia as prazoDia,
            s.flagObrigatorio as flagObrigatorio,
            s.codigo as codigo,
            s.dmEstado as dmEstado,
            s.idEntExterna as idEntExterna,
            s.possuiTaxa as possuiTaxa,
            e.denominacao as entidadeDenominacao,
            e.sigla as entidadeSigla,
            e.dmTipoEnt as entidadeDmTipoEnt
        from ZeeTTpSolicitacaoEntity s
        join ZeeTTpSolicRepreEntity r on r.idTpSolic = s.id
        left join ZeeTEntidadeExternaEntity e on e.id = s.idEntExterna
        where lower(r.dmTpRepresentante) = lower(:dmTpRepresentante)
        order by s.nome
        """)
    List<ServicoProjection> findServicosByTipoRepresentante(@Param("dmTpRepresentante") String dmTpRepresentante);
}
