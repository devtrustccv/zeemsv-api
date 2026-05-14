package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTParamReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ZeeTParamReportRepository extends JpaRepository<ZeeTParamReportEntity, Integer>, JpaSpecificationExecutor<ZeeTParamReportEntity> {
}
