package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTZonaCordEntity;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ZeeTZonaCordRepository extends JpaRepository<ZeeTZonaCordEntity, Integer>, JpaSpecificationExecutor<ZeeTZonaCordEntity> {
    List<ZeeTZonaCordEntity> findByIdZonaInOrderByIdZonaAscVerticeAsc(Collection<Integer> idZonas);
}
