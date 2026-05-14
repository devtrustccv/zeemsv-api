package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTAtividadeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ZeeTAtividadeRepository extends JpaRepository<ZeeTAtividadeEntity, Integer>, JpaSpecificationExecutor<ZeeTAtividadeEntity> {
}
