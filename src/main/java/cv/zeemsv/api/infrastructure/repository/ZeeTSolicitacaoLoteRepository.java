package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTSolicitacaoLoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ZeeTSolicitacaoLoteRepository extends JpaRepository<ZeeTSolicitacaoLoteEntity, Integer>, JpaSpecificationExecutor<ZeeTSolicitacaoLoteEntity> {
}
