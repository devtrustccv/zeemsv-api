package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTCobrancaPrestacaoEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ZeeTCobrancaPrestacaoRepository extends JpaRepository<ZeeTCobrancaPrestacaoEntity, Integer>, JpaSpecificationExecutor<ZeeTCobrancaPrestacaoEntity> {
    List<ZeeTCobrancaPrestacaoEntity> findByIdCobrancaInOrderByIdCobrancaAscIdAsc(List<Integer> idsCobranca);
}
