package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTInvestidorEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ZeeTInvestidorRepository extends JpaRepository<ZeeTInvestidorEntity, Integer>, JpaSpecificationExecutor<ZeeTInvestidorEntity> {
    @Query("""
        select distinct i
        from ZeeTInvestidorEntity i
        join ZeeTRepresInvestidorEntity r on r.idInvestidor = i.id
        join ZeeTUserEntity u on u.id = r.idUser
        where lower(u.email) = lower(:email)
        """)
    List<ZeeTInvestidorEntity> findByUserEmail(@Param("email") String email);
}
