package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTSolicOnboardingEntity;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ZeeTSolicOnboardingRepository extends JpaRepository<ZeeTSolicOnboardingEntity, Integer>, JpaSpecificationExecutor<ZeeTSolicOnboardingEntity> {
    List<ZeeTSolicOnboardingEntity> findByIdTpSolicInOrderByOrdemAscIdAsc(Collection<Integer> idsTpSolic);
}
