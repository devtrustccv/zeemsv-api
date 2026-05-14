package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTLoteEnquadEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ZeeTLoteEnquadRepository extends JpaRepository<ZeeTLoteEnquadEntity, Integer>, JpaSpecificationExecutor<ZeeTLoteEnquadEntity> {
}
