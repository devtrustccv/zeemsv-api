package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTSolicitacaoCobrancaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ZeeTSolicitacaoCobrancaRepository extends JpaRepository<ZeeTSolicitacaoCobrancaEntity, Integer>, JpaSpecificationExecutor<ZeeTSolicitacaoCobrancaEntity> {
    List<ZeeTSolicitacaoCobrancaEntity> findByIdSolicitacao(Integer idSolicitacao);

    List<ZeeTSolicitacaoCobrancaEntity> findByIdCobranca(Integer idCobranca);

    List<ZeeTSolicitacaoCobrancaEntity> findByIdCobrancaIn(List<Integer> idsCobranca);

    void deleteByIdSolicitacao(Integer idSolicitacao);

    void deleteByIdCobranca(Integer idCobranca);
}
