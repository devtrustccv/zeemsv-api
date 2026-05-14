package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTTpSolicRelacaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ZeeTTpSolicRelacaoRepository extends JpaRepository<ZeeTTpSolicRelacaoEntity, Integer>, JpaSpecificationExecutor<ZeeTTpSolicRelacaoEntity> {
}
