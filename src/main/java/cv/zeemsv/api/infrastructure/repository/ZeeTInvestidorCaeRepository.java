package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTInvestidorCaeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ZeeTInvestidorCaeRepository extends JpaRepository<ZeeTInvestidorCaeEntity, Integer>, JpaSpecificationExecutor<ZeeTInvestidorCaeEntity> {
}
