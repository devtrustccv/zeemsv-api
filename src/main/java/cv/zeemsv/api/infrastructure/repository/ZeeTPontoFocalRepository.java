package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTPontoFocalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ZeeTPontoFocalRepository extends JpaRepository<ZeeTPontoFocalEntity, Integer>, JpaSpecificationExecutor<ZeeTPontoFocalEntity> {
}
