package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTTaxaCondicoesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ZeeTTaxaCondicoesRepository extends JpaRepository<ZeeTTaxaCondicoesEntity, Integer>, JpaSpecificationExecutor<ZeeTTaxaCondicoesEntity> {
}
