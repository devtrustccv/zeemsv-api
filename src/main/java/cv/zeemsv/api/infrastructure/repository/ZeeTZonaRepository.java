package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTZonaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ZeeTZonaRepository extends JpaRepository<ZeeTZonaEntity, Integer>, JpaSpecificationExecutor<ZeeTZonaEntity> {
    List<ZeeTZonaEntity> findAllByOrderByNomeAsc();
}
