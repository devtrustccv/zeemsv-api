package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTSocioRepresEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ZeeTSocioRepresRepository extends JpaRepository<ZeeTSocioRepresEntity, Integer>, JpaSpecificationExecutor<ZeeTSocioRepresEntity> {
    List<ZeeTSocioRepresEntity> findByNomeContainingIgnoreCase(String nome);

    List<ZeeTSocioRepresEntity> findByNif(String nif);

    List<ZeeTSocioRepresEntity> findByNrDoc(String nrDoc);

    List<ZeeTSocioRepresEntity> findByEmailIgnoreCase(String email);

    List<ZeeTSocioRepresEntity> findByIdUser(Integer idUser);

    Optional<ZeeTSocioRepresEntity> findFirstByIdUserOrderByIdDesc(Integer idUser);

    List<ZeeTSocioRepresEntity> findByNomeContainingIgnoreCaseAndNrDoc(String nome, String nrDoc);
}
