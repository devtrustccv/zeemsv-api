package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTCobrancaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ZeeTCobrancaRepository extends JpaRepository<ZeeTCobrancaEntity, Integer>, JpaSpecificationExecutor<ZeeTCobrancaEntity> {
}
