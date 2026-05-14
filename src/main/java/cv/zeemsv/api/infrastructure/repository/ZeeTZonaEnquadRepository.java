package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTZonaEnquadEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ZeeTZonaEnquadRepository extends JpaRepository<ZeeTZonaEnquadEntity, Integer>, JpaSpecificationExecutor<ZeeTZonaEnquadEntity> {
}
