package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.TblCaeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TblCaeRepository extends JpaRepository<TblCaeEntity, Integer>, JpaSpecificationExecutor<TblCaeEntity> {
}
