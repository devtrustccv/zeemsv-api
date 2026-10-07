package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTParkEntity;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ZeeTParkRepository extends JpaRepository<ZeeTParkEntity, Integer>, JpaSpecificationExecutor<ZeeTParkEntity> {
    List<ZeeTParkEntity> findByIdZonaInOrderByIdZonaAscNomeAsc(Collection<Integer> idZonas);
}
