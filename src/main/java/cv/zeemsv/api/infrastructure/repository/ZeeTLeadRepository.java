package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTLeadEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ZeeTLeadRepository extends JpaRepository<ZeeTLeadEntity, Integer>, JpaSpecificationExecutor<ZeeTLeadEntity> {
}
