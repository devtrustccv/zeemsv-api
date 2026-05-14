package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTDocRelacaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ZeeTDocRelacaoRepository extends JpaRepository<ZeeTDocRelacaoEntity, Integer>, JpaSpecificationExecutor<ZeeTDocRelacaoEntity> {
}
