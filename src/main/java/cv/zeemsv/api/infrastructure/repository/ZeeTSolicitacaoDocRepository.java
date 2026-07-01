package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTSolicitacaoDocEntity;
import cv.zeemsv.api.infrastructure.repository.projection.SolicitacaoDocProjection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ZeeTSolicitacaoDocRepository extends JpaRepository<ZeeTSolicitacaoDocEntity, Integer>, JpaSpecificationExecutor<ZeeTSolicitacaoDocEntity> {
    @Query("""
        select
            sd.id as id,
            :idSolicitacao as idSolicitacao,
            tstd.id as idTpSolicTpDoc,
            tstd.idTpDoc as idTpDoc,
            td.nome as tpDocNome,
            td.codigo as tpDocCodigo,
            tstd.requisito as requisito,
            tstd.flagObrigatorio as flagObrigatorio,
            tstd.pedResp as pedResp,
            sd.idProcesso as idProcesso,
            sd.idEtapa as idEtapa,
            sd.dataRegisto as dataRegisto,
            sd.userRegisto as userRegisto,
            sd.path as path
        from ZeeTTpSolicTpDocEntity tstd
        left join ZeeTTpDocEntity td on td.id = tstd.idTpDoc
        left join ZeeTSolicitacaoDocEntity sd on sd.idSolicitacao = :idSolicitacao and sd.idTpSolicTpDoc = tstd.id
        where tstd.idTpDoc is not null
          and tstd.pedResp = 'PEDIDO'
          and tstd.dmEstado = 'A'
          and tstd.idTpSolic = :idTpSolicitacao
        order by tstd.id asc
        """)
    List<SolicitacaoDocProjection> findDetalheByIdSolicitacao(
        @Param("idSolicitacao") Integer idSolicitacao,
        @Param("idTpSolicitacao") Integer idTpSolicitacao
    );
}
