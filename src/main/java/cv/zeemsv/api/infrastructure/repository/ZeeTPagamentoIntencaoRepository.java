package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTPagamentoIntencaoEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ZeeTPagamentoIntencaoRepository extends JpaRepository<ZeeTPagamentoIntencaoEntity, Integer>, JpaSpecificationExecutor<ZeeTPagamentoIntencaoEntity> {
    Optional<ZeeTPagamentoIntencaoEntity> findByIntentionId(String intentionId);

    Optional<ZeeTPagamentoIntencaoEntity> findByTransactionId(String transactionId);
}
