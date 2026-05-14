package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTLoteProprietarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ZeeTLoteProprietarioRepository extends JpaRepository<ZeeTLoteProprietarioEntity, Integer>, JpaSpecificationExecutor<ZeeTLoteProprietarioEntity> {
}
