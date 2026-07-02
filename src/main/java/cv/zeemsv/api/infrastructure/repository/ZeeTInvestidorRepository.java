package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTInvestidorEntity;
import cv.zeemsv.api.infrastructure.repository.projection.InvestidorUserProjection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ZeeTInvestidorRepository extends JpaRepository<ZeeTInvestidorEntity, Integer>, JpaSpecificationExecutor<ZeeTInvestidorEntity> {
    @Query("""
        select
            i.id as id,
            i.denominacao as denominacao,
            i.nif as nif,
            i.email as email,
            i.telemovel as telemovel,
            i.dmEstado as dmEstado,
            i.dmTipoInvestidor as dmTipoInvestidor,
            i.paisOrigem as paisOrigem,
            i.endereco as endereco,
            r.dmTpRepresentante as dmTpRepresentante,
            r.dmPrincipal as dmPrincipal
        from ZeeTInvestidorEntity i
        join ZeeTRepresInvestidorEntity r on r.idInvestidor = i.id
        join ZeeTUserEntity u on u.id = r.idUser
        where lower(u.email) = lower(:email)
            and i.dmEstado = 'A'
            and r.dmEstado = 'A'
        """)
    List<InvestidorUserProjection> findByUserEmail(@Param("email") String email);

    List<ZeeTInvestidorEntity> findByDenominacaoContainingIgnoreCase(String denominacao);

    List<ZeeTInvestidorEntity> findByNrDocumento(String nrDocumento);

    List<ZeeTInvestidorEntity> findByDenominacaoContainingIgnoreCaseAndNrDocumento(String denominacao, String nrDocumento);

    List<ZeeTInvestidorEntity> findByNif(String nif);
}
