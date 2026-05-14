package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTProjLoteEnquadEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ZeeTProjLoteEnquadRepository extends JpaRepository<ZeeTProjLoteEnquadEntity, Integer>, JpaSpecificationExecutor<ZeeTProjLoteEnquadEntity> {
}
