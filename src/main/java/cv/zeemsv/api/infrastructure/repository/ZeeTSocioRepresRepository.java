package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTSocioRepresEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ZeeTSocioRepresRepository extends JpaRepository<ZeeTSocioRepresEntity, Integer>, JpaSpecificationExecutor<ZeeTSocioRepresEntity> {
    List<ZeeTSocioRepresEntity> findByNomeContainingIgnoreCase(String nome);

    List<ZeeTSocioRepresEntity> findByNrDoc(String nrDoc);

    List<ZeeTSocioRepresEntity> findByNomeContainingIgnoreCaseAndNrDoc(String nome, String nrDoc);
}
