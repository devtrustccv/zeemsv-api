package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTAtividadeEntity;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ZeeTAtividadeRepository extends JpaRepository<ZeeTAtividadeEntity, Integer>, JpaSpecificationExecutor<ZeeTAtividadeEntity> {
    List<ZeeTAtividadeEntity> findByIdInvestidorAndDmTipoAtividadeIgnoreCaseOrderByDataCreateDescIdDesc(BigDecimal idInvestidor, String dmTipoAtividade);

    List<ZeeTAtividadeEntity> findByIdInvestidorAndAgendamentoTrueOrderByDataCreateDescIdDesc(BigDecimal idInvestidor);
}
