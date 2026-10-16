package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTProjAtividadeEntity;
import cv.zeemsv.api.infrastructure.repository.projection.AtividadeProjetoProjection;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ZeeTProjAtividadeRepository extends JpaRepository<ZeeTProjAtividadeEntity, Integer>, JpaSpecificationExecutor<ZeeTProjAtividadeEntity> {
    @Query("""
        select distinct pa.idAtividade as idAtividade,
            p.id as idProjeto, p.denominacao as denominacao
        from ZeeTProjAtividadeEntity pa
        join ZeeTProjInvestEntity p on p.id = pa.idProjeto
        where pa.idAtividade in :idsAtividades
            and pa.dmEstado in ('A', 'ATIVO')
        order by pa.idAtividade, p.id
        """)
    List<AtividadeProjetoProjection> findProjetosByAtividadeIds(@Param("idsAtividades") List<Integer> idsAtividades);
}
