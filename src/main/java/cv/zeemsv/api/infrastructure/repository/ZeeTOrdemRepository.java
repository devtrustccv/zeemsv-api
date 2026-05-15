package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTOrdemEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ZeeTOrdemRepository extends JpaRepository<ZeeTOrdemEntity, Integer>, JpaSpecificationExecutor<ZeeTOrdemEntity> {
    List<ZeeTOrdemEntity> findAllByOrderByNomeAsc();
}
