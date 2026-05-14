package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTConfigTemplateNotifEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ZeeTConfigTemplateNotifRepository extends JpaRepository<ZeeTConfigTemplateNotifEntity, Integer>, JpaSpecificationExecutor<ZeeTConfigTemplateNotifEntity> {
}
