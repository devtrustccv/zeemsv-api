package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTProjAtividadeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ZeeTProjAtividadeRepository extends JpaRepository<ZeeTProjAtividadeEntity, Integer>, JpaSpecificationExecutor<ZeeTProjAtividadeEntity> {
}
