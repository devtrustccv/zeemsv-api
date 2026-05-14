package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTTpSolicTaxaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ZeeTTpSolicTaxaRepository extends JpaRepository<ZeeTTpSolicTaxaEntity, Integer>, JpaSpecificationExecutor<ZeeTTpSolicTaxaEntity> {
}
