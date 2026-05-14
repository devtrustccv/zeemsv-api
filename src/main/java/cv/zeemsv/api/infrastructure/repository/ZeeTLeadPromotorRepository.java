package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTLeadPromotorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ZeeTLeadPromotorRepository extends JpaRepository<ZeeTLeadPromotorEntity, Integer>, JpaSpecificationExecutor<ZeeTLeadPromotorEntity> {
}
