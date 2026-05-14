package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTPFocalTpSolicEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ZeeTPFocalTpSolicRepository extends JpaRepository<ZeeTPFocalTpSolicEntity, Integer>, JpaSpecificationExecutor<ZeeTPFocalTpSolicEntity> {
}
