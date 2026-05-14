package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTInvestidorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ZeeTInvestidorRepository extends JpaRepository<ZeeTInvestidorEntity, Integer>, JpaSpecificationExecutor<ZeeTInvestidorEntity> {
}
