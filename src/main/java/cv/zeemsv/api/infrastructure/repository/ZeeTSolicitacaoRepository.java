package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTSolicitacaoEntity;
import cv.zeemsv.api.infrastructure.repository.projection.SolicitacaoInvestidorProjection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ZeeTSolicitacaoRepository extends JpaRepository<ZeeTSolicitacaoEntity, Integer>, JpaSpecificationExecutor<ZeeTSolicitacaoEntity> {
    List<ZeeTSolicitacaoEntity> findByIdInvestidorOrderByDataSolicDesc(Integer idInvestidor);
    Optional<ZeeTSolicitacaoEntity> findFirstByIdPedido(Integer idPedido);

    @Query("""
        select
            s.id as id,
            tp.nome as nome,
            tp.descricao as descricao,
            s.dmEstadoProc as estado,
            s.idTpSolicitacao as idTpSolicitacao,
            s.idPedido as idPedido,
            s.idEntidade as idEntidade,
            s.idOrganica as idOrganica,
            s.idProcesso as idProcesso,
            s.idSolicPai as idSolicPai,
            s.idPromotor as idPromotor,
            s.idInvestidor as idInvestidor,
            s.idProjeto as idProjeto,
            s.exposicao as exposicao,
            s.dmOrigem as dmOrigem,
            s.userSolic as userSolic,
            s.dataSolic as dataSolic,
            s.dataPrevResposta as dataPrevResposta,
            s.descSolic as descSolic,
            s.dmEstadoProc as dmEstadoProc,
            s.dataResposta as dataResposta,
            s.userResposta as userResposta,
            s.descResposta as descResposta,
            s.prazoDia as prazoDia,
            s.prazoReal as prazoReal,
            coalesce(s.etapaAtual, p.etapaAtual) as etapaAtual,
            s.idPontoFocalResp as idPontoFocalResp,
            s.flagCorrecao as flagCorrecao,
            s.dataEnvioCorrecao as dataEnvioCorrecao,
            s.dataFimPrevistaCorrecao as dataFimPrevistaCorrecao,
            s.dataCorrecao as dataCorrecao,
            s.userCorecao as userCorecao,
            tp.nome as tpSolicitacaoNome,
            tp.descricao as tpSolicitacaoDescricao,
            tp.codigo as tpSolicitacaoCodigo,
            tp.dmTipoSolicitacao as tpSolicitacaoDmTipo,
            tp.dmEstado as tpSolicitacaoDmEstado,
            i.denominacao as investidorDenominacao,
            i.nif as investidorNif,
            i.email as investidorEmail,
            i.telemovel as investidorTelemovel,
            i.paisOrigem as investidorPaisOrigem,
            pr.denominacao as promotorDenominacao,
            pr.nif as promotorNif,
            pr.email as promotorEmail,
            pr.telemovel as promotorTelemovel,
            pr.paisOrigem as promotorPaisOrigem,
            proj.denominacao as projetoDenominacao,
            proj.dmRegime as projetoDmRegime,
            proj.dmProdutoServico as projetoDmProdutoServico,
            proj.dmEstadoProc as projetoDmEstadoProc,
            proj.dmSituacao as projetoDmSituacao,
            p.dmEstadoPedido as pedidoDmEstadoPedido,
            p.etapaAtual as pedidoEtapaAtual,
            p.codEtapaAtual as pedidoCodEtapaAtual,
            p.dtRegisto as pedidoDtRegisto,
            p.dtDespacho as pedidoDtDespacho,
            p.dtFim as pedidoDtFim,
            p.obsDespacho as pedidoObsDespacho,
            p.resultado as pedidoResultado,
            p.requerente as pedidoRequerente
        from ZeeTSolicitacaoEntity s
        left join ZeeTTpSolicitacaoEntity tp on tp.id = s.idTpSolicitacao
        left join ZeeTInvestidorEntity i on i.id = s.idInvestidor
        left join ZeeTLeadPromotorEntity pr on pr.id = s.idPromotor
        left join ZeeTProjInvestEntity proj on proj.id = s.idProjeto
        left join TPedidoEntity p on p.id = s.idPedido
        where s.idInvestidor = :idInvestidor
        order by s.dataSolic desc, s.id desc
        """)
    List<SolicitacaoInvestidorProjection> findDetalheByInvestidorId(@Param("idInvestidor") Integer idInvestidor);
}
