package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTLoteRelacaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ZeeTLoteRelacaoRepository extends JpaRepository<ZeeTLoteRelacaoEntity, Integer>, JpaSpecificationExecutor<ZeeTLoteRelacaoEntity> {
}
