package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTLeadFasesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ZeeTLeadFasesRepository extends JpaRepository<ZeeTLeadFasesEntity, Integer>, JpaSpecificationExecutor<ZeeTLeadFasesEntity> {
}
