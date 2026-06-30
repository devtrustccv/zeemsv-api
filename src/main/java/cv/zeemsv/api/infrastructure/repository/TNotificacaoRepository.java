package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.TNotificacaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TNotificacaoRepository extends JpaRepository<TNotificacaoEntity, Integer>, JpaSpecificationExecutor<TNotificacaoEntity> {
}
