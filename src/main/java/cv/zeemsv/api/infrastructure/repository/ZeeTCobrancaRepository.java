package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTCobrancaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ZeeTCobrancaRepository extends JpaRepository<ZeeTCobrancaEntity, Integer>, JpaSpecificationExecutor<ZeeTCobrancaEntity> {
    List<ZeeTCobrancaEntity> findByIdInvestidorOrderByDataEmissaoDescIdDesc(Integer idInvestidor);
}
