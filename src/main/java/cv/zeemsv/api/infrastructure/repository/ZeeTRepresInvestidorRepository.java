package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTRepresInvestidorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ZeeTRepresInvestidorRepository extends JpaRepository<ZeeTRepresInvestidorEntity, Integer>, JpaSpecificationExecutor<ZeeTRepresInvestidorEntity> {
}
