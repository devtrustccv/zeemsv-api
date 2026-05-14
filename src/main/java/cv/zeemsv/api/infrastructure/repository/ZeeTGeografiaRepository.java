package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTGeografiaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ZeeTGeografiaRepository extends JpaRepository<ZeeTGeografiaEntity, String>, JpaSpecificationExecutor<ZeeTGeografiaEntity> {
}
