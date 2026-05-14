package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTPagamentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ZeeTPagamentoRepository extends JpaRepository<ZeeTPagamentoEntity, Integer>, JpaSpecificationExecutor<ZeeTPagamentoEntity> {
}
