package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTEntidadeExternaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ZeeTEntidadeExternaRepository extends JpaRepository<ZeeTEntidadeExternaEntity, Integer>, JpaSpecificationExecutor<ZeeTEntidadeExternaEntity> {
}
