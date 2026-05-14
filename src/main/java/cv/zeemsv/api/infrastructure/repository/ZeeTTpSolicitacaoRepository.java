package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTTpSolicitacaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ZeeTTpSolicitacaoRepository extends JpaRepository<ZeeTTpSolicitacaoEntity, Integer>, JpaSpecificationExecutor<ZeeTTpSolicitacaoEntity> {
}
