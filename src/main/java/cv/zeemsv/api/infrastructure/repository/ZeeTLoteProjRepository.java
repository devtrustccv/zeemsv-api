package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTLoteProjEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ZeeTLoteProjRepository extends JpaRepository<ZeeTLoteProjEntity, Integer>, JpaSpecificationExecutor<ZeeTLoteProjEntity> {
}
