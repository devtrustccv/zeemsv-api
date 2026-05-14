package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTSolicitacaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ZeeTSolicitacaoRepository extends JpaRepository<ZeeTSolicitacaoEntity, Integer>, JpaSpecificationExecutor<ZeeTSolicitacaoEntity> {
}
