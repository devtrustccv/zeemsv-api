package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTDocRelacaoEntity;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ZeeTDocRelacaoRepository extends JpaRepository<ZeeTDocRelacaoEntity, Integer>, JpaSpecificationExecutor<ZeeTDocRelacaoEntity> {
    List<ZeeTDocRelacaoEntity> findByTipoRelacaoAndIdRelacaoAndEstado(String tipoRelacao, BigDecimal idRelacao, String estado);

    List<ZeeTDocRelacaoEntity> findByTipoRelacaoAndIdRelacaoAndEstadoOrderByDateCreateDescIdDesc(String tipoRelacao, BigDecimal idRelacao, String estado);

    Optional<ZeeTDocRelacaoEntity> findByIdTpDocAndIdRelacaoAndTipoRelacao(Integer idTpDoc, BigDecimal idRelacao, String tipoRelacao);

    Optional<ZeeTDocRelacaoEntity> findFirstByPath(String path);
}
