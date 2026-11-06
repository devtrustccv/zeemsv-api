package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTCobrancaEntity;
import java.util.List;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ZeeTCobrancaRepository extends JpaRepository<ZeeTCobrancaEntity, Integer>, JpaSpecificationExecutor<ZeeTCobrancaEntity> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from ZeeTCobrancaEntity c where c.id = :id")
    Optional<ZeeTCobrancaEntity> findByIdForPagamento(@Param("id") Integer id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from ZeeTCobrancaEntity c where c.id in :ids order by c.id")
    List<ZeeTCobrancaEntity> findAllForPagamento(@Param("ids") List<Integer> ids);

    List<ZeeTCobrancaEntity> findByIdInvestidorOrderByDataEmissaoDescIdDesc(Integer idInvestidor);
}
