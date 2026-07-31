package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTSolicitacaoLoteEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ZeeTSolicitacaoLoteRepository extends JpaRepository<ZeeTSolicitacaoLoteEntity, Integer>, JpaSpecificationExecutor<ZeeTSolicitacaoLoteEntity> {
    List<ZeeTSolicitacaoLoteEntity> findByIdSolicitacao(Integer idSolicitacao);

    void deleteByIdSolicitacao(Integer idSolicitacao);
}
