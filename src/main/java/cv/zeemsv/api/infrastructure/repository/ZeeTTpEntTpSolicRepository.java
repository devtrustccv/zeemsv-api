package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTTpEntTpSolicEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ZeeTTpEntTpSolicRepository extends JpaRepository<ZeeTTpEntTpSolicEntity, Integer>, JpaSpecificationExecutor<ZeeTTpEntTpSolicEntity> {
}
