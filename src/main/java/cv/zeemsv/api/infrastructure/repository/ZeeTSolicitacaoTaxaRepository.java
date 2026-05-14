package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTSolicitacaoTaxaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ZeeTSolicitacaoTaxaRepository extends JpaRepository<ZeeTSolicitacaoTaxaEntity, Integer>, JpaSpecificationExecutor<ZeeTSolicitacaoTaxaEntity> {
}
