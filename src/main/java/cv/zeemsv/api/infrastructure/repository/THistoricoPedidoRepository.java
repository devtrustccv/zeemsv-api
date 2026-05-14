package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.THistoricoPedidoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface THistoricoPedidoRepository extends JpaRepository<THistoricoPedidoEntity, Integer>, JpaSpecificationExecutor<THistoricoPedidoEntity> {
}
