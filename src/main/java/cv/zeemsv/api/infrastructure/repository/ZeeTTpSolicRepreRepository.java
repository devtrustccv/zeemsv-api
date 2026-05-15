package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTTpSolicRepreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ZeeTTpSolicRepreRepository extends JpaRepository<ZeeTTpSolicRepreEntity, Integer>, JpaSpecificationExecutor<ZeeTTpSolicRepreEntity> {
}
