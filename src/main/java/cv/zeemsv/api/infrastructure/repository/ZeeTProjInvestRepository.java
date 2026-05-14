package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTProjInvestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ZeeTProjInvestRepository extends JpaRepository<ZeeTProjInvestEntity, Integer>, JpaSpecificationExecutor<ZeeTProjInvestEntity> {
}
