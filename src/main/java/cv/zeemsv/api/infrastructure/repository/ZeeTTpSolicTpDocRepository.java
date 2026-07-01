package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTTpSolicTpDocEntity;
import cv.zeemsv.api.infrastructure.repository.projection.SolicitacaoDocumentoConfiguradoProjection;
import cv.zeemsv.api.infrastructure.repository.projection.SolicitacaoRequisitoProjection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ZeeTTpSolicTpDocRepository extends JpaRepository<ZeeTTpSolicTpDocEntity, Integer>, JpaSpecificationExecutor<ZeeTTpSolicTpDocEntity> {
    @Query("""
        select
            tstd.id as idTpSolicTpDoc,
            tstd.idTpDoc as idTpDoc,
            td.nome as tpDocNome,
            td.codigo as tpDocCodigo,
            tstd.requisito as requisito,
            tstd.flagObrigatorio as flagObrigatorio,
            tstd.pedResp as pedResp
        from ZeeTTpSolicTpDocEntity tstd
        left join ZeeTTpDocEntity td on td.id = tstd.idTpDoc
        where tstd.idTpDoc is not null
          and tstd.pedResp = 'PEDIDO'
          and tstd.dmEstado = 'A'
          and tstd.idTpSolic = :idTpSolicitacao
        order by tstd.id asc
        """)
    List<SolicitacaoDocumentoConfiguradoProjection> findDocumentosByIdTpSolicitacao(@Param("idTpSolicitacao") Integer idTpSolicitacao);

    @Query("""
        select
            tstd.id as idTpSolicTpDoc,
            tstd.requisito as requisito,
            tstd.flagObrigatorio as flagObrigatorio
        from ZeeTTpSolicTpDocEntity tstd
        where tstd.requisito is not null
          and tstd.dmEstado = 'A'
          and tstd.idTpSolic = :idTpSolicitacao
        order by tstd.id asc
        """)
    List<SolicitacaoRequisitoProjection> findRequisitosByIdTpSolicitacao(@Param("idTpSolicitacao") Integer idTpSolicitacao);
}
