package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTLoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ZeeTLoteRepository extends JpaRepository<ZeeTLoteEntity, Integer>, JpaSpecificationExecutor<ZeeTLoteEntity> {
}
