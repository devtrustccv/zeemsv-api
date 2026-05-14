package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTTpDocEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ZeeTTpDocRepository extends JpaRepository<ZeeTTpDocEntity, Integer>, JpaSpecificationExecutor<ZeeTTpDocEntity> {
}
