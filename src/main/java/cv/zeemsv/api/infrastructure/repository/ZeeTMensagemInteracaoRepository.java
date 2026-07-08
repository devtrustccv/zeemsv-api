package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTMensagemInteracaoEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ZeeTMensagemInteracaoRepository extends JpaRepository<ZeeTMensagemInteracaoEntity, Integer>, JpaSpecificationExecutor<ZeeTMensagemInteracaoEntity> {
    List<ZeeTMensagemInteracaoEntity> findByIdInteracaoOrderByDataEnvioAscIdAsc(Integer idInteracao);
}
