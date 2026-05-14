package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTSocioRepresEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ZeeTSocioRepresRepository extends JpaRepository<ZeeTSocioRepresEntity, Integer>, JpaSpecificationExecutor<ZeeTSocioRepresEntity> {
}
