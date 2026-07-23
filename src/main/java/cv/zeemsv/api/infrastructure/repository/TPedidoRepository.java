package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.TPedidoEntity;
import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TPedidoRepository extends JpaRepository<TPedidoEntity, Integer>, JpaSpecificationExecutor<TPedidoEntity> {
    Optional<TPedidoEntity> findFirstByIdProcessoAndTipoRelacaoIgnoreCaseOrderByIdDesc(BigDecimal nrProcesso, String tipoRelacao);
}
