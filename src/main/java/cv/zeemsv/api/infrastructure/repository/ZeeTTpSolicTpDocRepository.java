package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTTpSolicTpDocEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ZeeTTpSolicTpDocRepository extends JpaRepository<ZeeTTpSolicTpDocEntity, Integer>, JpaSpecificationExecutor<ZeeTTpSolicTpDocEntity> {
}
