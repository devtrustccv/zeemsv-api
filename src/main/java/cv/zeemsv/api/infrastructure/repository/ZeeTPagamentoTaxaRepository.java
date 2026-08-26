package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTPagamentoTaxaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ZeeTPagamentoTaxaRepository extends JpaRepository<ZeeTPagamentoTaxaEntity, Integer>, JpaSpecificationExecutor<ZeeTPagamentoTaxaEntity> {
    List<ZeeTPagamentoTaxaEntity> findByIdPagamentoInOrderByIdPagamentoAscIdAsc(List<Integer> idsPagamento);
}
