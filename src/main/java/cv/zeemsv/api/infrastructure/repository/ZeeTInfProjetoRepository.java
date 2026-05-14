package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTInfProjetoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ZeeTInfProjetoRepository extends JpaRepository<ZeeTInfProjetoEntity, Integer>, JpaSpecificationExecutor<ZeeTInfProjetoEntity> {
}
