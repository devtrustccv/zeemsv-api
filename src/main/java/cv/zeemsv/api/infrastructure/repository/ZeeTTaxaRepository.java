package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTTaxaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ZeeTTaxaRepository extends JpaRepository<ZeeTTaxaEntity, Integer>, JpaSpecificationExecutor<ZeeTTaxaEntity> {
}
