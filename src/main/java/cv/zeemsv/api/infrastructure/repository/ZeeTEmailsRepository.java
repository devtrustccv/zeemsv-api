package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTEmailsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ZeeTEmailsRepository extends JpaRepository<ZeeTEmailsEntity, Integer>, JpaSpecificationExecutor<ZeeTEmailsEntity> {
}
