package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTTpSolicRepreEntity;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ZeeTTpSolicRepreRepository extends JpaRepository<ZeeTTpSolicRepreEntity, Integer>, JpaSpecificationExecutor<ZeeTTpSolicRepreEntity> {
    List<ZeeTTpSolicRepreEntity> findByIdTpSolicIn(Collection<Integer> idsTpSolic);
}
