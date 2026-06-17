package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTSessionEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ZeeTSessionRepository extends JpaRepository<ZeeTSessionEntity, Integer> {
    Optional<ZeeTSessionEntity> findBySessionToken(String sessionToken);
}
