package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTAtividadeEntity;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ZeeTAtividadeRepository extends JpaRepository<ZeeTAtividadeEntity, Integer>, JpaSpecificationExecutor<ZeeTAtividadeEntity> {
    List<ZeeTAtividadeEntity> findByIdInvestidorAndDmTipoAtividadeIgnoreCaseOrderByDataCreateDescIdDesc(BigDecimal idInvestidor, String dmTipoAtividade);

    List<ZeeTAtividadeEntity> findByIdInvestidorAndAgendamentoTrueOrderByDataCreateDescIdDesc(BigDecimal idInvestidor);

    @Query("""
        select a from ZeeTAtividadeEntity a
        where upper(a.dmTipoAtividade) = upper(:dmTipoAtividade)
          and (
              (:idUser is not null and a.idUser = :idUser)
              or (:idInvestidor is not null and a.idInvestidor = :idInvestidor)
              or (:email is not null and lower(a.email) = lower(:email))
          )
        order by a.dataCreate desc, a.id desc
        """)
    List<ZeeTAtividadeEntity> findInteracoes(
        @Param("dmTipoAtividade") String dmTipoAtividade,
        @Param("idUser") Integer idUser,
        @Param("idInvestidor") BigDecimal idInvestidor,
        @Param("email") String email
    );
}
