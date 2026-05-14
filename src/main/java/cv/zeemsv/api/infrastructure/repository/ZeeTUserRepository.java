package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ZeeTUserRepository extends JpaRepository<ZeeTUserEntity, Integer>, JpaSpecificationExecutor<ZeeTUserEntity> {
}
