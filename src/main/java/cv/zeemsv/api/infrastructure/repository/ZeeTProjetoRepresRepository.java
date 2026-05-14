package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTProjetoRepresEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ZeeTProjetoRepresRepository extends JpaRepository<ZeeTProjetoRepresEntity, Integer>, JpaSpecificationExecutor<ZeeTProjetoRepresEntity> {
}
